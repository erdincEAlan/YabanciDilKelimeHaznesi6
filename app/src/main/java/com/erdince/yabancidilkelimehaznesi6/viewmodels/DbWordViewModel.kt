package com.erdince.yabancidilkelimehaznesi6.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import com.google.firebase.firestore.FieldPath
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
import kotlin.math.log

@HiltViewModel
class DbWordViewModel @Inject constructor(savedStateHandle: SavedStateHandle?, application: Application) : ViewModel() {
    private var responseCode: Int = 400
    var wordLiveData = MutableLiveData<ResourceModel<Any?>>()
    private val wordData = MutableStateFlow<WordModel?>(null)
    var publicWordData : StateFlow<WordModel?> = wordData
    private var resource : ResourceModel<Any?> = ResourceModel(false, null)
    private var wordList = mutableListOf<WordModel>()
    private val db : FirebaseFirestore = Firebase.firestore
    private val userDb = db.collection("users")
    private val customWordsDb = db.collection("customWords")
    private val publicWordsDb = db.collection("preparedWords")
    private val localDbController: WordDao = LocalWordDb.getInstance(application.baseContext).wordDao()
    private var app = application
     private fun syncLocalWithCloudDb() {
        CoroutineScope(Dispatchers.IO).launch {
            localDbController.getWordList(WordType.CustomWord.value).let {
                if (it.isEmpty()){
                    getWordList(WordType.CustomWord.value, dbSource = dbSources.Cloud.source, syncTheLocalDb = true)
                }
            }
        }

    }
    fun syncDatabases(){
        syncCloudWithLocal()
        CoroutineScope(Dispatchers.IO).launch { syncLocalWithCloudDb() }
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
                                    customWordsDb.add(word).addOnSuccessListener {
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

    private suspend fun syncLocalWithCloud() {
        CoroutineScope(Dispatchers.IO).launch {
            if (resource.success) {
                (resource.data as MutableList<WordModel>).let { cloudList ->

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
                localDbController?.updateWord(increasedWord)
                customWordsDb.document(word.wordId ).update("wordPoint", FieldValue.increment(1))
                if (increasedWord.wordPoint!! >= 10){
                    localDbController?.updateWord(increasedWord.apply { wordLearningStatus = true })
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
                if (decreasedWord.wordPoint!! >= 10){
                    customWordsDb.document(word.wordId).update("wordLearningStatus", true)
                    increaseLearnedWordsCount()
                }
            }
        }
    }

    fun deleteWord(wordId: String) : Int{
        CoroutineScope(Dispatchers.IO).launch { localDbController.apply {
            getWordById(wordId)?.let { delete(it) }
        }
        }
        customWordsDb.document(wordId).delete().addOnSuccessListener {
            responseCode = 200
        }.addOnFailureListener {
            responseCode = 400
        }
        return responseCode
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
    fun getRandomWord(wordSourceType : String, lastWordId : String? = null) {
        if (wordSourceType == WordType.CustomWord.value) {
            generateCustomWord(lastWordId)
        } else if (wordSourceType == WordType.PreparedWord.value) {
            generatePreparedWord(lastWordId)
        }
    }

    private fun generatePreparedWord(lastWordId: String?) {
        publicWordsDb.whereEqualTo("wordStatus", true).get().addOnSuccessListener { documents ->
            for (document in documents) {
                wordList.add(document.toObject<WordModel>())
            }
            if (wordList.size > 0) {
                resource.success = true
                while (resource.data == null) {
                    wordList.random().let {
                        if (wordList.size > 1) {
                            if (it.wordId != lastWordId) {
                                resource.data = it
                            }
                        } else resource.data = it;
                    }
                }

                wordLiveData.postValue(resource)

            } else wordLiveData.postValue(resource)

        }.addOnFailureListener() {
            wordLiveData.postValue(resource)
        }
    }

    private fun generateCustomWord(lastWordId: String? = ""){
        CoroutineScope(Dispatchers.IO).launch {
            localDbController.getQuizWord().let { word->
                if (word !=null){
                    if (lastWordId != word.wordId){
                        resource.success = true
                        resource.data = word
                        wordLiveData.postValue(resource)
                    }else{
                        CoroutineScope(Dispatchers.IO).launch {
                            generateCustomWord(lastWordId)
                        }
                    }
                }else{
                    customWordsDb.whereEqualTo("wordStatus", true).whereEqualTo("wordLearningStatus", false)
                        .whereEqualTo("wordOwnerId", Firebase.auth.uid).get().addOnSuccessListener { documents ->
                            wordList = documents.toObjects(WordModel::class.java)
                            if (wordList.size > 0) {
                                resource.success = true
                                while (resource.data == null) {
                                    wordList.random().let {
                                        if (wordList.size > 1) {
                                            if (it.wordId != lastWordId) {
                                                resource.data = it
                                            }
                                        } else resource.data = it;
                                    }
                                }

                                wordLiveData.postValue(resource)

                            } else wordLiveData.postValue(resource)

                        }.addOnFailureListener() {
                            wordLiveData.postValue(resource)
                        }
                }
                wordData.value = resource.data as WordModel?
            }
        }
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
                            resource.data = wordList
                            resource.success = true
                            if (syncTheLocalDb) CoroutineScope(Dispatchers.IO).launch{ syncLocalWithCloud() }
                        }
                        wordLiveData.postValue(resource)

                    }.addOnFailureListener() { wordLiveData.postValue(resource) }
                }
            }

            dbSources.Local.source -> {
                (app as YDKHApp).returnLocalDbController()
                    ?.getWordList(WordType.CustomWord.value, learnedStatus = learnedStatus)?.let {
                        if (it.isNotEmpty()) {
                            wordList = it.toMutableList()
                            resource.data = it.toMutableList()
                            resource.success = true
                            wordLiveData.postValue(resource)
                        } else {
                            getWordList(WordType.CustomWord.value, dbSource = dbSources.Cloud.source, syncTheLocalDb = true)
                        }
                    }

            }
        }


    }

    fun resetTheWordStatus(word: WordModel?) {
        word?.merge(WordModel(wordLearningStatus = false, wordPoint = 0))
        updateWord(word)
    }
    fun addPublicWordToCustomWord (word : WordModel){
        word.wordStatus = true
        word.wordLearningStatus = false
        word.wordPoint = 0
        word.wordOwnerId = Firebase.auth.uid
        customWordsDb.add(word).addOnSuccessListener {
            word.wordId = it.id
            it.set(word)
            increaseTotalWordsCount()
        }
    }
    fun addCustomWord (word : WordModel){
        word.wordStatus = true
        word.wordLearningStatus = false
        word.wordPoint = 0
        word.wordOwnerId = Firebase.auth.uid
        word.wordType = "customWord"
        word.wordId = UUID.randomUUID().toString()
        customWordsDb.document(word.wordId).set(word).addOnSuccessListener {
            CoroutineScope(Dispatchers.IO).launch { localDbController.insertAll(word) }
            increaseTotalWordsCount()
        }.addOnFailureListener{message ->
            Log.d("FIRESTORE EXCEPTION", message.message.toString())
        }

    }

    private fun increaseTotalWordsCount() {
        userDb.document(Firebase.auth.uid.toString()).update("totalWordCount", FieldValue.increment(1))
    }
    private fun increaseLearnedWordsCount() {
        userDb.document(Firebase.auth.uid.toString()).update("learnedWordsCount", FieldValue.increment(1))
    }

}