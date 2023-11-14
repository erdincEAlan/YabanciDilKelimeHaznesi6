package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TestSonucActivity: AppCompatActivity() {
    var db : FirebaseFirestore?=null
    private var kelimeKendi : String? = null
    private var kelimeAnlam : String? = null
    private var kelimeOrnek : String? = null
    private var  kelimelerRef : CollectionReference? = null
    private var kelimeKendiTextView : TextView?=null
    private var kelimeAnlamTextView : TextView?=null
    private var kelimeOrnekCumleTextView : TextView?=null
    private var bitirButton : Button?= null
    private var sonrakiKelimeButton : Button?= null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_sonuc)


        init()
    }


    private fun setUI() {
        kelimeKendiTextView = findViewById(R.id.kelimeKendiTextView)
        kelimeAnlamTextView = findViewById(R.id.kelimeAnlamTextView)
        kelimeOrnekCumleTextView = findViewById(R.id.kelimeOrnekCumleTextView)
        bitirButton = findViewById(R.id.addToMyCustomWords)
        sonrakiKelimeButton = findViewById(R.id.sonrakiKelimeButton)
    }


    fun init() {
        prepare()
        initUI()
    }

    private fun prepare() {
        setFirebase()
        setDocumentReferences()
        getIntentExtras()
    }

    private fun initUI() {
        setUI()
        initTextViews()
        setButtonClickers()
    }

    private fun setButtonClickers() {
        bitirButton?.setOnClickListener {
            switchActivity("AnaEkranActivity")
        }
        sonrakiKelimeButton?.setOnClickListener {
            switchActivity("TestActivity")
        }
    }

    private fun initTextViews() {
        kelimeKendiTextView?.text = (kelimeKendi)
        kelimeAnlamTextView?.text = kelimeAnlam
        kelimeOrnekCumleTextView?.text = (kelimeOrnek)
    }

    private fun getIntentExtras() {
        kelimeKendi = intent.getStringExtra("kelimeKendi").toString()
        kelimeAnlam = intent.getStringExtra("kelimeAnlam").toString()
        kelimeOrnek = intent.getStringExtra("kelimeOrnek").toString()
    }

    private fun setDocumentReferences() {
        kelimelerRef = db?.collection("kelimeler")
    }
    private fun setFirebase() {
        db = Firebase.firestore
    }
}