package com.erdince.yabancidilkelimehaznesi6.viewmodels

import android.app.Application
import android.content.Context
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
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class DbWordViewModel @Inject constructor(savedStateHandle: SavedStateHandle?, application: Application) : ViewModel() {
    private var responseCode: Int = 400
    private var word : WordModel? = null
    var wordLiveData = MutableLiveData<ResourceModel<Any?>>()
    private var resource : ResourceModel<Any?> = ResourceModel(false, null)
    private var wordList = mutableListOf<WordModel>()
    private val db : FirebaseFirestore = Firebase.firestore
    private val userDb = db.collection("users")
    private val customWordsDb = db.collection("customWords")
    private val publicWordsDb = db.collection("preparedWords")
    private var localDb: LocalWordDb? = null
    private var localDbController: WordDao? = null
    private var app = application
    suspend fun syncLocalWithCloudDb(context: Context) {

        localDbController = LocalWordDb.getInstance(context).wordDao()
        //------
        getWordList(WordType.CustomWord.value, dbSource = dbSources.Cloud.source, syncTheLocalDb = true)

    }

    private suspend fun syncLocalWithCloud() {
        if (resource.success) {
            (resource.data as MutableList<WordModel>).let { cloudList ->

                localDbController?.getAll().let { localWordList ->
                    if (localWordList != null) {
                        for (word in localWordList) {
                            localDbController?.delete(word)
                        }
                    }
                }
                for (word in cloudList) {
                    localDbController?.insertAll(word)
                }

            }
        }
    }

    fun getWordFromId (id : String, wordType : String){
        if (wordType == WordType.PreparedWord.value) {
            publicWordsDb.document(id).get().addOnSuccessListener {
                if (it.toObject<WordModel>() != null) {
                    resource.success = true
                    resource.data = it.toObject<WordModel>()
                    wordLiveData.postValue(resource)
                }
            }
        } else if (wordType == WordType.CustomWord.value) {
            customWordsDb.document(id).get().addOnSuccessListener(){
                if (it.toObject<WordModel>() != null) {
                    resource.success = true
                    resource.data = it.toObject<WordModel>()
                    wordLiveData.postValue(resource)
                }
            }
        }
    }
    fun increaseWordPoint(word : WordModel){
        customWordsDb.document(word.wordId.toString() ).update("wordPoint", FieldValue.increment(1))
        if (word.wordPoint?.plus(1)!! >= 10){
            customWordsDb.document(word.wordId.toString()).update("wordLearningStatus", true)
            increaseLearnedWordsCount()
        }

    }
    fun deleteWord(wordId: String) : Int{
        customWordsDb.document(wordId).delete().addOnSuccessListener {
            responseCode = 200
        }.addOnFailureListener {
            responseCode = 400
        }
        return responseCode
    }

    fun updateWord(word: WordModel?): Int {
        word?.let {
            customWordsDb.document(word.wordId!!).set(word).addOnSuccessListener {
                responseCode = 200
            }.addOnFailureListener {
                responseCode = 400
            }
            return responseCode
        }
        return responseCode
    }
    fun observeRandomWord(wordSourceType : String, lastWordId : String? = null) {
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

    private fun generateCustomWord(lastWordId: String?) {
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
                            if (syncTheLocalDb) CoroutineScope(Dispatchers.IO).launch { syncLocalWithCloud() }
                        }
                        wordLiveData.postValue(resource)

                    }.addOnFailureListener() { wordLiveData.postValue(resource) }
                }
            }

            dbSources.Local.source -> {
                (app as YDKHApp).returnLocalDbController()
                    ?.getWordList(WordType.CustomWord.value)?.let {
                        if (it.isNotEmpty()) {
                            resource.data = it.toMutableList()
                            resource.success = true
                            wordLiveData.postValue(resource)
                        } else {
                            getWordList(WordType.CustomWord.value, dbSource = dbSources.Cloud.source)
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
        customWordsDb.add(word).addOnSuccessListener {
            word.wordId = it.id
            it.set(word)
            increaseTotalWordsCount()
        }
    }

    private fun increaseTotalWordsCount() {
        userDb.document(Firebase.auth.uid.toString()).update("totalWordCount", FieldValue.increment(1))
    }
    private fun increaseLearnedWordsCount() {
        userDb.document(Firebase.auth.uid.toString()).update("learnedWordsCount", FieldValue.increment(1))
    }

}