package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.restartActivity
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue.increment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*


class TestActivity : AppCompatActivity() {
    private var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private lateinit var uid: String
    private var kelimelerRef : CollectionReference?=null
    private var kullaniciRef : CollectionReference?=null
    private var testSonucIntent: Intent? = null
    private var soruKelime: KelimeModel? = null
    private var soruListe = mutableListOf<KelimeModel?>()
    private var cevapKelime: String? = null
    private var cevapButon: Button? = null
    private var soruKelimeTextView: TextView? = null
    private var cevapEditText: EditText? = null
    private var backButton : ImageButton?=null
    private var nextButton : Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val fragment : FragmentQuizSourceSelection = FragmentQuizSourceSelection.newInstance("sdf","sdf")
       changeFragment(fragment)
        //init()
    }
    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.quizFragmentContainer, fragment)
        fragmentTransaction.commit()
    }

/*
    fun init() {
        prapare()
        initUI()
    }

    private fun prapare() {
        setFirebase()
        setDatabaseReferences()
        takeListAndSetQuestKelime()
    }

    private fun initUI() {
        setUI()
        setIntents()
        setButtonClickers()
    }

    private fun setUI() {
        cevapButon = findViewById(R.id.cevapButton)
        soruKelimeTextView = findViewById(R.id.soruKelimeTextView)
        cevapEditText = findViewById(R.id.cevapKelimeEditText)
        nextButton = findViewById(R.id.sonrakiKelimeButton)
    }

    private fun takeListAndSetQuestKelime() {
        kelimelerRef?.whereEqualTo("kelimeDurum", 1)?.whereEqualTo("kelimeOgrenmeDurum", 0)
            ?.whereEqualTo("kelimeSahipID", uid)?.get()?.addOnSuccessListener { documents ->
                for (document in documents) {
                    soruKelime = document.toObject()
                    soruKelime?.kelimeID = document.id
                    soruListe.add(soruKelime!!)
                }
                if (soruKelime == null) {
                    makeToast("Sormak için kelime bulunmadığı için anaekrana yönlendirildi. Kelime Ekle ekranından yeni kelime ekleyebilirsiniz")
                    switchActivity("AnaEkranActivity")
                } else {
                    soruKelime = soruListe.random()
                    soruKelimeTextView?.text = soruKelime?.kelimeKendi?.capitalize(Locale.getDefault())
                }
            }

    }

    private fun setButtonClickers() {
        backButton?.setOnClickListener {
            switchActivity("AnaEkranActivity")
        }
        nextButton?.setOnClickListener {
            increaseKelimePointAndSwitch()
        }
        cevapButon?.setOnClickListener {
            takeTheAnswerAndInit()
        }
    }

    private fun takeTheAnswerAndInit() {
        setStringsFromEditTexts()
        if (takeStringsAndMakeReadyToQuestioning(soruKelime?.kelimeAnlam!!)== takeStringsAndMakeReadyToQuestioning(cevapKelime!!)) {
            correctAnswer()
        } else {
            incorrectAnswer()
        }
    }
    private fun takeStringsAndMakeReadyToQuestioning(text:String): String{
        return text.lowercase().replace("\\p{Punct}|\\s".toRegex(), "")
    }
    private fun incorrectAnswer() {
        makeToast("Cevap Yanlış")
        testSonucPutExtrasAndStart()
    }

    private fun testSonucPutExtrasAndStart() {
        testSonucIntent?.putExtra("kelimeKendi", soruKelime?.kelimeKendi)
        testSonucIntent?.putExtra("kelimeAnlam", soruKelime?.kelimeAnlam)
        testSonucIntent?.putExtra("kelimeOrnek", soruKelime?.kelimeOrnekCumle)
        startActivity(testSonucIntent)
    }

    private fun setStringsFromEditTexts() {
        cevapKelime = cevapEditText?.text.toString()
    }

    private fun correctAnswer() {
        makeToast("Cevap Doğru")
        increaseKelimePointAndSwitch()
    }

    private fun increaseKelimePointAndSwitch() {
        kelimelerRef?.document(soruKelime?.kelimeID.toString())?.update("kelimePuan", increment(1))
        checkKelimeLearnedAndUpdate()
        makeToast("Kelimeye puan eklendi")
        restartActivity()
    }

    private fun checkKelimeLearnedAndUpdate() {
        if (soruKelime?.kelimePuan == 6) {
            setKelimeLearned()
            updateUserOgrenilenKelimeNumber()
        }
    }

    private fun setKelimeLearned() {
        kelimelerRef?.document(soruKelime?.kelimeID.toString())?.update("kelimeOgrenmeDurum", 1)
    }

    private fun updateUserOgrenilenKelimeNumber() {
        kullaniciRef?.document(Firebase.auth.currentUser!!.uid)?.update(
            "ogrenilenKelimeSayisi",
            increment(1)
        )
    }

    private fun setIntents() {
        testSonucIntent = Intent(this@TestActivity, TestSonucActivity::class.java)
    }

    private fun setFirebase() {
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user?.uid.toString()
    }
    private fun setDatabaseReferences(){
        kelimelerRef = db?.collection("kelimeler")
        kullaniciRef = db?.collection("user")
    }

 */
}