package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.app
import java.util.*

@HiltViewModel
class DbWordViewModel @Inject constructor(): ViewModel() {
var word : KelimeModel? = null
    var soruListe : MutableList<KelimeModel>?=null
    private val db : FirebaseFirestore = Firebase.firestore
    private val customWordsDb = db.collection("kelimeler")
    private val publicWordsDb = db.collection("preparedWords")
    var wordLiveData = MutableLiveData<KelimeModel>()



    fun getWordFromId (id : String, wordType : String){
        if (wordType == "preparedWords"){
            publicWordsDb.document(id).get().addOnSuccessListener {
                if (it.toObject<KelimeModel>() != null) {
                    wordLiveData.postValue(it.toObject<KelimeModel>())
                }
            }
        }else if(wordType == "kelimeler"){
            customWordsDb.document(id).get().addOnSuccessListener(){
                if (it.toObject<KelimeModel>() != null) {
                    wordLiveData.postValue(it.toObject<KelimeModel>())

                }
            }
        }


    }
    fun observeRandomWord(wordSourceType : String): Boolean{
        if (wordSourceType == "kelimeler"){
            customWordsDb.whereEqualTo("kelimeDurum", 1).whereEqualTo("kelimeOgrenmeDurum", 0)
                .whereEqualTo("kelimeSahipID", Firebase.auth.uid).get().addOnSuccessListener { documents ->
                   soruListe = documents.toObjects(KelimeModel::class.java)
                    if(soruListe?.size!! >0){ wordLiveData.postValue(soruListe!!.random()) }

                }
        }else if (wordSourceType == "preparedWords"){
            publicWordsDb.whereEqualTo("kelimeDurum", 1).get().addOnSuccessListener { documents ->
                for (document in documents){
                    soruListe?.add(document.toObject(KelimeModel::class.java))
                }
                if(soruListe?.size!! >0 ) wordLiveData.postValue(soruListe!!.random())
            }
        }
        return (soruListe?.size!! > 0)
    }
    fun addCustomWord (word : KelimeModel){
        word.kelimeOgrenmeDurum = 0
        word.kelimePuan = 1
        word.kelimeSahipID = Firebase.auth.uid
        word.wordType = "preparedWord"
        customWordsDb.add(word)
    }



}