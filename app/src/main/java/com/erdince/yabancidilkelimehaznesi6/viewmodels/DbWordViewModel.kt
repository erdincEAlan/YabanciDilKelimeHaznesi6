package com.erdince.yabancidilkelimehaznesi6.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject

@HiltViewModel
class DbWordViewModel @Inject constructor(savedStateHandle: SavedStateHandle?): ViewModel() {
    private var responseCode : Int = 0
    private var word : WordModel? = null
    var wordLiveData = MutableLiveData<ResourceModel<Any?>>()
    private var resource : ResourceModel<Any?> = ResourceModel(false, null)
    private var wordList = mutableListOf<WordModel>()
    private val db : FirebaseFirestore = Firebase.firestore
    private val userDb = db.collection("users")
    private val customWordsDb = db.collection("customWords")
    private val publicWordsDb = db.collection("preparedWords")



    fun getWordFromId (id : String, wordType : String){
        if (wordType == "preparedWord"){
            publicWordsDb.document(id).get().addOnSuccessListener {
                if (it.toObject<WordModel>() != null) {
                    resource.success = true
                    resource.data = it.toObject<WordModel>()
                    wordLiveData.postValue(resource)
                }
            }
        }else if(wordType == "customWord"){
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
    fun updateWord(word : WordModel, wordId : String) : Int{
        word.wordId=wordId
        customWordsDb.document(wordId).set(word).addOnSuccessListener {
            responseCode = 200
        }.addOnFailureListener {
            responseCode = 400
        }
       return responseCode
    }
    fun observeRandomWord(wordSourceType : String, lastWordId : String? = null) {

        if (wordSourceType == "customWord") {
            customWordsDb.whereEqualTo("wordStatus", true).whereEqualTo("wordLearningStatus", false)
                .whereEqualTo("wordOwnerId", Firebase.auth.uid).get().addOnSuccessListener { documents ->
                    wordList = documents.toObjects(WordModel::class.java)
                    if (wordList.size > 0) {
                        resource.success = true
                        while (resource.data == null){
                            wordList.random().let {
                                if (wordList.size >1){
                                    if (it.wordId != lastWordId){
                                        resource.data = it
                                    }
                                }else resource.data = it ;
                            }
                        }

                        wordLiveData.postValue(resource)

                    }else wordLiveData.postValue(resource)

                }.addOnFailureListener(){
                    wordLiveData.postValue(resource)
                }
        } else if (wordSourceType == "preparedWord") {
            publicWordsDb.whereEqualTo("wordStatus", true).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    wordList.add(document.toObject<WordModel>())
                }
                if (wordList.size > 0) {
                    resource.success = true
                    while (resource.data !=null){
                        wordList.random().let {
                            if (wordList.size >1){
                                if (it.wordId != lastWordId){
                                    resource.data = it
                                }
                            }else resource.data = it
                        }
                    }

                    wordLiveData.postValue(resource)

                }else wordLiveData.postValue(resource)

            }.addOnFailureListener(){

                wordLiveData.postValue(resource)
            }

        }

    }

    fun getWordList(wordType : String, learnedStatus : Boolean? = null) {
        if (wordType == "customWord") {
            var listRequest = customWordsDb.whereEqualTo("wordStatus", true).whereEqualTo("wordOwnerId",Firebase.auth.uid)
                if (learnedStatus !=null){
                    listRequest = listRequest.whereEqualTo("wordLearningStatus",learnedStatus)
                }
                listRequest.get().addOnSuccessListener { documents ->
                    wordList = documents.toObjects(WordModel::class.java)
                    if (wordList.size>0){
                        resource.data = wordList
                        resource.success = true
                    }
                    wordLiveData.postValue(resource)

                }.addOnFailureListener() { wordLiveData.postValue(resource)}
        }
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