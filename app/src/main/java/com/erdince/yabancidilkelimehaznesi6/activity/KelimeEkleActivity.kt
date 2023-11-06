package com.erdince.yabancidilkelimehaznesi6.activity


import android.os.Bundle
import android.widget.EditText

import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.restartActivity
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue.increment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class KelimeEkleActivity : AppCompatActivity() {

    private var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private lateinit var uid: String
    private var userRef: DocumentReference? = null
    private var backButton: ImageButton? = null
    private var kaydetButton: ImageButton? = null
    private var kendiEditText: EditText? = null
    private var anlamEditText: EditText? = null
    private var ornekEditText: EditText? = null
    private lateinit var kelimeTxt: String
    private lateinit var anlamTxt: String
    private lateinit var ornekTxt: String
    private var eklenecekKelime: KelimeModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelime_ekle)
        init()

    }

    private fun init() {
        setFirebase()
        initUI()
    }

    private fun initUI() {
        setUI()
        setButtonClickers()
    }

    private fun setUI() {
        backButton = findViewById(R.id.kelimeEkleBackButton)
        kaydetButton = findViewById(R.id.kelimeKaydetButton)
        kendiEditText = findViewById(R.id.kelimeEkleKelimeGiris)
        anlamEditText = findViewById(R.id.kelimeEkleAnlamGiris)
        ornekEditText = findViewById(R.id.kelimeEkleOrnekCumleGiris)
    }

    private fun setButtonClickers() {
        kaydetButton?.setOnClickListener {
            saveKelime()
            restartActivity()
        }
        backButton?.setOnClickListener {
            switchActivity("AnaEkranActivity")
        }
    }

    private fun saveKelime() {
        setStringsFromEditTexts()
        setNewKelime()
        addNewKelimeToDatabase()
        updateUserKelimeSayi()
    }

    private fun setStringsFromEditTexts() {
        kelimeTxt = kendiEditText?.text.toString().trimStart().trimEnd()
        anlamTxt = anlamEditText?.text.toString().trimStart().trimEnd()
        ornekTxt = ornekEditText?.text.toString().trimStart().trimEnd()
    }

    private fun setNewKelime() {
        eklenecekKelime = KelimeModel("0", kelimeTxt, anlamTxt, ornekTxt, 1, 0, uid, 0)
    }

    private fun addNewKelimeToDatabase() {
        db?.collection("kelimeler")?.add(eklenecekKelime!!)
        setKelimeIdAndUpload()

        makeToast("Kelime başarıyla eklendi")
    }

    private fun setKelimeIdAndUpload() {
        db?.collection("kelimeler")?.whereEqualTo("kelimeSahipID", uid)
            ?.whereEqualTo("kelimeKendi", kelimeTxt)?.get()?.addOnSuccessListener { documents ->
                for (document in documents) {
                    eklenecekKelime = document.toObject()
                    eklenecekKelime?.kelimeID = document.id
                }
                uploadTheFinalKelime()
            }
    }

    private fun uploadTheFinalKelime() {
        db?.collection("kelimeler")?.document(eklenecekKelime?.kelimeID!!)
            ?.update("kelimeID", eklenecekKelime?.kelimeID)
    }

    private fun updateUserKelimeSayi() {
        userRef?.update("kelimeSayisi", increment(1))
    }

    private fun setUser() {
        takeUserDocumentReference()
    }

    private fun takeUserDocumentReference() {
        userRef = db?.collection("user")?.document(uid)
    }


    private fun setFirebase() {
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user?.uid.toString()
        setUser()
    }
}