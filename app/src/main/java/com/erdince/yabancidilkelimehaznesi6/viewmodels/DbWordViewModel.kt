package com.erdince.yabancidilkelimehaznesi6.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject

@HiltViewModel
class DbWordViewModel @Inject constructor(savedStateHandle: SavedStateHandle?): ViewModel() {
    var word : WordModel? = null
    var wordLiveData = MutableLiveData<ResourceModel>()
    var resource : ResourceModel = ResourceModel(false, word)
    private var wordList = mutableListOf<WordModel>()
    private val db : FirebaseFirestore = Firebase.firestore
    private val customWordsDb = db.collection("kelimeler")
    private val publicWordsDb = db.collection("preparedWords")




    fun getWordFromId (id : String, wordType : String){
        if (wordType == "preparedWords"){
            publicWordsDb.document(id).get().addOnSuccessListener {
                if (it.toObject<WordModel>() != null) {
                    resource.success = true
                    resource.data = it.toObject<WordModel>()
                    wordLiveData.postValue(resource)
                }
            }
        }else if(wordType == "customWords"){
            customWordsDb.document(id).get().addOnSuccessListener(){
                if (it.toObject<WordModel>() != null) {
                    resource.success = true
                    resource.data = it.toObject<WordModel>()
                    wordLiveData.postValue(resource)
                }
            }
        }


    }
    fun updateWord(word : WordModel, wordId : String){
        word.kelimeID=wordId
        publicWordsDb.document(wordId).set(word)

    }
    fun observeRandomWord(wordSourceType : String) {

        if (wordSourceType == "customWords") {
            customWordsDb.whereEqualTo("kelimeDurum", 1).whereEqualTo("kelimeOgrenmeDurum", 0)
                .whereEqualTo("kelimeSahipID", Firebase.auth.uid).get().addOnSuccessListener { documents ->
                    wordList = documents.toObjects(WordModel::class.java)
                    if (wordList.size > 0) {
                        resource.success = true
                        resource.data = wordList.random()
                        wordLiveData.postValue(resource)

                    }else wordLiveData.postValue(resource)

                }.addOnFailureListener(){
                    wordLiveData.postValue(resource)
                }
        } else if (wordSourceType == "preparedWords") {
            publicWordsDb.whereEqualTo("kelimeDurum", 1).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    wordList.add(document.toObject<WordModel>())
                }
                if (wordList.size > 0) {
                    resource.success = true
                    resource.data = wordList.random()
                    wordLiveData.postValue(resource)

                }else wordLiveData.postValue(resource)

            }.addOnFailureListener(){

                wordLiveData.postValue(resource)
            }

        }

    }

    fun getWordList(wordType : String) {
        if (wordType == "customWords") {
            customWordsDb.whereEqualTo("kelimeDurum", 1).whereEqualTo("wordType", "customWord").get()
                .addOnSuccessListener { documents ->
                    wordList = documents.toObjects(WordModel::class.java)
                    if (wordList.size>0){
                        resource.data = wordList
                        resource.success = true
                    }
                    wordLiveData.postValue(resource)

                }.addOnFailureListener() { wordLiveData.postValue(resource)}
        } else if (wordType == "preparedWords") {
            publicWordsDb.whereEqualTo("kelimeDurum", 1).whereEqualTo("wordType", "customWord").get()
                .addOnSuccessListener { documents ->
                    resource.success = true
                    wordList = documents.toObjects(WordModel::class.java)
                    resource.data = wordList
                    wordLiveData.postValue(resource)
                }.addOnFailureListener { wordLiveData.postValue(resource) }

        }

    }

    fun addPublicWordToCustomWord (word : WordModel){
        word.kelimeOgrenmeDurum = 0
        word.kelimePuan = 0
        word.kelimeSahipID = Firebase.auth.uid
        word.wordType = "preparedWord"
        customWordsDb.add(word)
    }
    fun addCustomWord (word : WordModel){
        word.kelimeOgrenmeDurum = 0
        word.kelimePuan = 0
        word.kelimeSahipID = Firebase.auth.uid
        word.wordType = "customWord"
        customWordsDb.add(word)
    }



}