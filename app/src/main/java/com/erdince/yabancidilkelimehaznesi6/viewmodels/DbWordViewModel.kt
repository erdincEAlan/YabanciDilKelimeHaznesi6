package com.erdince.yabancidilkelimehaznesi6.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.erdince.yabancidilkelimehaznesi6.YDKHApp
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.WordType
import com.erdince.yabancidilkelimehaznesi6.util.dbSources
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.WordDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DbWordViewModel @Inject constructor(savedStateHandle: SavedStateHandle?, application: Application) : ViewModel() {
    private var responseCode: Int = 400
    var wordLiveData = MutableLiveData<ResourceModel<Any?>>()
    //Wrong answer quiz
    private val wrongAnswerWordData = MutableStateFlow<WordModel?>(null)
    var publicWrongAnswerWordData : StateFlow<WordModel?> = wrongAnswerWordData
    //Quiz
    private val privateQuizWord = MutableStateFlow<ResourceModel<WordModel?>?>(null)
    var quizWord : StateFlow<ResourceModel<WordModel?>?> = privateQuizWord
    //
    private var resource : ResourceModel<Any?> = ResourceModel(false, null)
    private var wordList = mutableListOf<WordModel>()
    private val db : FirebaseFirestore = Firebase.firestore
    private val userDb = db.collection("users")
    private val customWordsDb = db.collection("customWords")
    private val publicWordsDb = db.collection("preparedWords")
    private val localDbController: WordDao = LocalWordDb.getInstance(application.baseContext).wordDao()
    private var app = application


    fun syncDatabases(){
        syncCloudWithLocal()
        CoroutineScope(Dispatchers.IO).launch {
            syncLocalWithCloudDb()
            /*
            wordList.size.let {totalWord->
                Firebase.auth.uid?.let {uid->
                    LocalUserDb.getInstance(app.baseContext).userDao().apply {
                        getUserDataById(uid = uid)?.let { updateUserData(it.apply { totalWordCount=totalWord }) }
                    }
                }
            }
             */
        }
    }
    private fun syncCloudWithLocal(){
        CoroutineScope(Dispatchers.IO).launch {
            getWordList(WordType.CustomWord.value, dbSource = dbSources.Local.source)
            if (wordList.size > 0){
                customWordsDb.whereEqualTo("wordOwnerId",Firebase.auth.uid).get()
                    .addOnSuccessListener { documents ->
                        val batch = db.batch()
                        for (document in documents) {
                            batch.delete(document.reference)
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                Log.d("Firestore", "All documents deleted successfully!")
                                wordList.forEachIndexed {index, word ->
                                    customWordsDb.document().delete()
                                    customWordsDb.document(word.wordId).set(word).addOnSuccessListener {
                                        Log.d("LOCAL TO CLOUD", "$index word okke")
                                    }
                                }
                            }
                            .addOnFailureListener { e -> Log.e("Firestore", "Error deleting documents", e) }
                    }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error fetching documents", e) }
            }
        }
    }
    private fun syncLocalWithCloudDb() {
        CoroutineScope(Dispatchers.IO).launch {
            localDbController.getWordList(WordType.CustomWord.value).let {
                if (it.isEmpty()){
                    wordList = it.toMutableList()
                    getWordList(WordType.CustomWord.value, dbSource = dbSources.Cloud.source, syncTheLocalDb = true)
                }
            }
        }

    }

    private suspend fun syncLocalWithCloud() {
        CoroutineScope(Dispatchers.IO).launch {
            if (resource.success) {
                (resource.data as MutableList<WordModel>).let { cloudList ->
                    wordList = cloudList
                    localDbController.getAll().let { localWordList ->
                        if (localWordList.isNotEmpty()) {
                            for (word in localWordList) {
                                localDbController.delete(word)
                            }
                        }
                    }
                    for (word in cloudList) {
                        localDbController.insertAll(word)
                    }

                }
            }
        }

    }

    fun getWordFromId (id : String, wordType : String){
        CoroutineScope(Dispatchers.IO).launch {
            if (wordType == WordType.PreparedWord.value) {
                publicWordsDb.document(id).get().addOnSuccessListener {
                    if (it.toObject<WordModel>() != null) {
                        resource.success = true
                        resource.data = it.toObject<WordModel>()
                        wordLiveData.postValue(resource)
                    }
                }.addOnFailureListener {
                    resource.success = false
                    wordLiveData.postValue(resource)
                }
            } else if (wordType == WordType.CustomWord.value) {
                localDbController.getWordById(id)?.let {
                    resource.success = true
                    resource.data = it
                    wordLiveData.postValue(resource)
                    return@launch
                }
                customWordsDb.document(id).get().addOnSuccessListener(){
                    if (it.toObject<WordModel>() != null) {
                        resource.success = true
                        resource.data = it.toObject<WordModel>()
                        wordLiveData.postValue(resource)
                    }
                }.addOnFailureListener {
                    resource.success = false
                    wordLiveData.postValue(resource)
                }
            }
        }


    }
    fun increaseWordPoint(word : WordModel){
        CoroutineScope(Dispatchers.IO).launch {
            word.wordPoint = word.wordPoint!! + 1
            word.let{ increasedWord ->
                localDbController.updateWord(increasedWord)
                customWordsDb.document(word.wordId ).update("wordPoint", FieldValue.increment(1))
                if (increasedWord.wordPoint!! >= 10){
                    localDbController.updateWord(increasedWord.apply { wordLearningStatus = true })
                    customWordsDb.document(word.wordId).update("wordLearningStatus", true)
                    increaseLearnedWordsCount()
                }
            }
        }

    }
    fun decreaseWordPoint(word : WordModel){
        CoroutineScope(Dispatchers.IO).launch {
            word.wordPoint?.let {point->
                if (point !=0){
                    word.wordPoint = point.minus(1)
                }
            }

            word.let{ decreasedWord ->
                localDbController.updateWord(decreasedWord)
                customWordsDb.document(word.wordId).update("wordPoint",decreasedWord.wordPoint)
            }
        }
    }

    fun deleteWord(wordId: String){
        CoroutineScope(Dispatchers.IO).launch { localDbController.apply {
            getWordById(wordId)?.let {
                delete(it)
            }
        }
            customWordsDb.document(wordId).delete()
        }

    }

    fun updateWord(word: WordModel?): Int {
        word?.let {
            CoroutineScope(Dispatchers.IO).launch {
                LocalWordDb.getInstance(app.baseContext).wordDao().apply {
                    getWordById(word.wordId)?.let {
                        delete(it)
                    }
                    insertAll(word)
                }
            }
            customWordsDb.document(word.wordId).set(word).addOnSuccessListener {
                responseCode = 200
            }.addOnFailureListener {
                responseCode = 400
            }
            return responseCode
        }
        return responseCode
    }
    fun getRandomWord(wordSourceType : String, lastWordId : String = "") {
        if (wordSourceType == WordType.CustomWord.value) {
            generateCustomWord(lastWordId)
        } else if (wordSourceType == WordType.PreparedWord.value) {
            generatePreparedWord(lastWordId)
        }
    }

    private fun generatePreparedWord(lastWordId: String?) {
        publicWordsDb.whereEqualTo("wordStatus", true).whereNotEqualTo("wordId",lastWordId).get().addOnSuccessListener { documents ->
            for (document in documents) {
                wordList.add(document.toObject<WordModel>())
            }
            if (wordList.size > 0) {
                    wordList.random().let {
                        if (wordList.size > 1) {
                            postStateValueAndClean(it)
                        }
                    }
            }
        }.addOnFailureListener() {
            postUnsuccessfulState()
        }
    }

    private fun generateCustomWord(lastWordId: String = ""){
        CoroutineScope(Dispatchers.IO).launch {
            localDbController.getQuizWord(lastWordId).let { word->
                if (word !=null){
                    postStateValueAndClean(word)
                }else{
                    customWordsDb.whereEqualTo("wordStatus", true).whereEqualTo("wordLearningStatus", false)
                        .whereEqualTo("wordOwnerId", Firebase.auth.uid).whereNotEqualTo("wordId",lastWordId).get().addOnSuccessListener { documents ->
                            wordList = documents.toObjects(WordModel::class.java)
                            if (wordList.size > 0) {
                                wordList.random().let {
                                    postStateValueAndClean(it)
                                    wrongAnswerWordData.value = it
                                }
                            }else{
                                postUnsuccessfulState()
                            }

                        }.addOnFailureListener() {
                            postUnsuccessfulState()
                        }
                }
                wordLiveData = MutableLiveData<ResourceModel<Any?>>()
            }
        }
    }

    private fun postUnsuccessful() {
        wordLiveData.value =  ResourceModel(false,null)
    }
    private fun postUnsuccessfulState(){
        privateQuizWord.value = ResourceModel(false,null)
    }

    private fun postStateValueAndClean(wordToPost : WordModel){
        val resourceToPost = ResourceModel<WordModel?>(true,wordToPost)
            privateQuizWord.value = resourceToPost
    }

    fun getWordList(
        wordType: String,
        learnedStatus: Boolean? = null,
        dbSource: String = dbSources.Local.source,
        syncTheLocalDb: Boolean = false
    ) {

        when (dbSource) {
            dbSources.Cloud.source -> {
                if (wordType == WordType.CustomWord.value) {
                    var listRequest =
                        customWordsDb.whereEqualTo("wordStatus", true).whereEqualTo("wordOwnerId", Firebase.auth.uid)
                    if (learnedStatus != null) {
                        listRequest = listRequest.whereEqualTo("wordLearningStatus", learnedStatus)
                    }
                    listRequest.get().addOnSuccessListener { documents ->
                        wordList = documents.toObjects(WordModel::class.java)
                        if (wordList.size > 0) {
                            if (syncTheLocalDb) CoroutineScope(Dispatchers.IO).launch{ syncLocalWithCloud() }
                        }
                        postListValueAndClean()
                    }.addOnFailureListener() {
                        resource.success = false
                        wordLiveData.postValue(resource)
                    }
                }

            }

            dbSources.Local.source -> {
                (app as YDKHApp).returnLocalWordDbController()
                    ?.getWordList(WordType.CustomWord.value, learnedStatus = learnedStatus)?.let {
                        if (it.isNotEmpty()) {
                            wordList = it.toMutableList()
                            postListValueAndClean()
                        } else {
                            getWordList(WordType.CustomWord.value, dbSource = dbSources.Cloud.source, syncTheLocalDb = true)
                        }
                    }

            }
        }

    }

    private fun postListValueAndClean() {
        val wordlistForResource = mutableListOf<WordModel>()
        wordlistForResource.addAll(wordList)
        resource.data = wordlistForResource
        resource.success = true
        wordLiveData.postValue(resource)
        wordList.clear()
    }
    private fun postWordValueAndClean(wordToPost: WordModel) {
        resource.data = wordToPost
        resource.success = true
        wordLiveData.postValue(resource)
    }

    fun resetTheWordStatus(word: WordModel?) {
        word?.merge(WordModel(wordLearningStatus = false, wordPoint = 0))
        updateWord(word)
    }
    fun addPublicWordToCustomWord (word : WordModel){
        addCustomWord(word)
        increaseTotalWordsCount()
    }
    fun addCustomWord (word : WordModel){
        word.wordStatus = true
        word.wordLearningStatus = false
        word.wordPoint = 0
        word.wordOwnerId = Firebase.auth.uid
        word.wordType = "customWord"
        word.wordId = UUID.randomUUID().toString()
        CoroutineScope(Dispatchers.IO).launch { localDbController.insertAll(word) }
        customWordsDb.document(word.wordId).set(word).addOnSuccessListener {
            Log.d("FIRESTORE","Word has been successfully added, wordId: ${word.wordId}")
        }.addOnFailureListener{message ->
            Log.d("FIRESTORE EXCEPTION", message.message.toString())
        }
        increaseTotalWordsCount()
    }

    private fun increaseTotalWordsCount() {
        CoroutineScope(Dispatchers.IO).launch {
            LocalUserDb.getInstance(app.baseContext).userDao().apply {
                getUserDataById(Firebase.auth.uid.toString())?.let {userData->
                    updateUserData(userData.apply { totalWordCount += 1 })
                }
            }
            userDb.document(Firebase.auth.uid.toString()).update("totalWordCount", FieldValue.increment(1))
        }
    }
    private fun increaseLearnedWordsCount() {
        CoroutineScope(Dispatchers.IO).launch {
            LocalUserDb.getInstance(app.baseContext).userDao().apply {
                getUserDataById(Firebase.auth.uid.toString())?.let {userData->
                    updateUserData(userData.apply { learnedWordsCount += 1})
                }
            }
            userDb.document(Firebase.auth.uid.toString()).update("learnedWordsCount", FieldValue.increment(1))
        }
    }
}