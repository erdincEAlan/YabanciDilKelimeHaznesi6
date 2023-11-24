package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import io.grpc.internal.SharedResourceHolder.Resource
import java.util.ResourceBundle

@HiltViewModel
class DbWordViewModel @Inject constructor(savedStateHandle: SavedStateHandle?): ViewModel() {
    var word : KelimeModel? = null
    var wordLiveData = MutableLiveData<ResourceModel>()
    var resource : ResourceModel = ResourceModel(false, word)
    private var wordList = mutableListOf<KelimeModel>()
    private val db : FirebaseFirestore = Firebase.firestore
    private val customWordsDb = db.collection("kelimeler")
    private val publicWordsDb = db.collection("preparedWords")




    fun getWordFromId (id : String, wordType : String){
        if (wordType == "preparedWords"){
            publicWordsDb.document(id).get().addOnSuccessListener {
                if (it.toObject<KelimeModel>() != null) {
                    resource.succes = true
                    resource.data = it.toObject<KelimeModel>()
                    wordLiveData.postValue(resource)
                }
            }
        }else if(wordType == "kelimeler"){
            customWordsDb.document(id).get().addOnSuccessListener(){
                if (it.toObject<KelimeModel>() != null) {
                    resource.succes = true
                    resource.data = it.toObject<KelimeModel>()
                    wordLiveData.postValue(resource)
                }
            }
        }


    }
    fun updateWord(word : KelimeModel, wordId : String){
        word.kelimeID=wordId
        publicWordsDb.document(wordId).set(word)

    }
    fun observeRandomWord(wordSourceType : String) {

        if (wordSourceType == "kelimeler") {
            customWordsDb.whereEqualTo("kelimeDurum", 1).whereEqualTo("kelimeOgrenmeDurum", 0)
                .whereEqualTo("kelimeSahipID", Firebase.auth.uid).get().addOnSuccessListener { documents ->
                    wordList = documents.toObjects(KelimeModel::class.java)
                    if (wordList.size > 0) {
                        resource.succes = true
                        resource.data = wordList.random()
                        wordLiveData.postValue(resource)

                    }else wordLiveData.postValue(resource)

                }.addOnFailureListener(){
                    wordLiveData.postValue(resource)
                }
        } else if (wordSourceType == "preparedWords") {
            publicWordsDb.whereEqualTo("kelimeDurum", 1).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    wordList.add(document.toObject<KelimeModel>())
                }
                if (wordList.size > 0) {
                    resource.succes = true
                    resource.data = wordList.random()
                    wordLiveData.postValue(resource)

                }else wordLiveData.postValue(resource)

            }.addOnFailureListener(){

                wordLiveData.postValue(resource)
            }

        }

    }
    fun addCustomWord (word : KelimeModel){
        word.kelimeOgrenmeDurum = 0
        word.kelimePuan = 1
        word.kelimeSahipID = Firebase.auth.uid
        word.wordType = "preparedWord"
        customWordsDb.add(word)
    }



}