package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.restartActivity
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_kelime_duzenle.*


class KelimeDuzenleActivity : AppCompatActivity() {
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    val uid = user?.uid
    private lateinit var kullaniciRef : DocumentReference
    var kelimeDocumentReference : DocumentReference?=null
    var kelimeEski: KelimeModel? = null
    private var kelimeId: String? = null
    private lateinit var kelimeKendiString: String
    private lateinit var kelimeAnlamString: String
    private lateinit var kelimeOrnekCumleString: String
    private var kelimeSilButon: Button? = null
    private var ogrenmeDurumButon: Button? = null
    private var duzenleKaydetButton: ImageButton? = null
    private var kelimeEdit: EditText? = null
    private var kelimeAnlamEdit: EditText? = null
    private var kelimeOrnekEdit: EditText? = null
    private var backButton: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelime_duzenle)
        init()

    }

    private fun init() {
        getIntentExtraAndSet()
        setDatabaseReferences()
        takeKelimeAndSet()
        initUI()
    }

    private fun initUI() {
        setUI()
        setButtonClickers()
    }

    private fun setButtonClickers() {
        backButton?.setOnClickListener {
            this.finish()
        }

        kelimeSilButon?.setOnClickListener {
            deleteKelime()

        }
        ogrenmeDurumButon?.setOnClickListener() {

            kelimeOgrenmeDurumReset()

        }
        duzenleKaydetButton?.setOnClickListener {
            setStringsFromEditTexts()
            updateKelime()
        }
    }

    private fun updateKelime() {
        kelimeDocumentReference?.update(
            "kelimeKendi",
            kelimeKendiString,
            "kelimeAnlam",
            kelimeAnlamString,
            "kelimeOrnekCumle",
            kelimeOrnekCumleString
        )?.addOnSuccessListener {
            makeToast("İşlem tamamlandı")
            restartActivity()
        }
    }

    private fun kelimeOgrenmeDurumReset() {
        kelimeDocumentReference?.update("kelimeOgrenmeDurum", 0, "kelimePuan", 0)
            ?.addOnSuccessListener() {
                kullaniciRef.update(
                    "ogrenilenKelimeSayisi",
                    FieldValue.increment(-1)
                )
                makeToast("İşlem Tamamlandı")
                restartActivity()
            }
    }

    private fun deleteKelime() {
        kelimeDocumentReference?.delete()?.addOnSuccessListener {
            makeToast("Kelime Silme Tamamlandı")
            switchActivity("KelimeListeActivity")
        }
    }

    private fun setDatabaseReferences() {
        kullaniciRef = db.collection("user").document(uid!!)
        kelimeDocumentReference = db.collection("kelimeler").document(kelimeId!!)
    }

    private fun takeKelimeAndSet() {
        kelimeDocumentReference?.get()?.addOnSuccessListener { kelimeDoc ->
            kelimeEski = kelimeDoc.toObject<KelimeModel>()
            setEditTextTexts()
            setOgrenmeButtonVisibility()
        }
    }

    private fun setOgrenmeButtonVisibility() {
        if (kelimeEski?.kelimeOgrenmeDurum == 1) {
            ogrenmeDurumButon?.visibility = View.VISIBLE
        } else {
            ogrenmeDurumButon?.visibility = View.GONE
        }
    }

    private fun setEditTextTexts() {
        kelimeEdit?.setText(kelimeEski?.kelimeKendi)
        kelimeAnlamEdit?.setText(kelimeEski?.kelimeAnlam)
        kelimeOrnekEdit?.setText(kelimeEski?.kelimeOrnekCumle)
    }

    private fun getIntentExtraAndSet() {
        kelimeId = intent.getStringExtra("kelimeID").toString()
    }

    private fun setStringsFromEditTexts() {
        kelimeKendiString = kelimeKendiEditText.text.toString()
        kelimeAnlamString = kelimeAnlamEditText.text.toString()
        kelimeOrnekCumleString = kelimeOrnekEditText.text.toString()
    }

    private fun setUI() {
        kelimeSilButon = findViewById(R.id.kelimeSilButton)
        ogrenmeDurumButon = findViewById(R.id.kelimeDuzenleOgrenilmeIsaretButton)
        duzenleKaydetButton = findViewById(R.id.kelimeDuzenKaydetButon)
        kelimeEdit = findViewById(R.id.kelimeKendiEditText)
        kelimeAnlamEdit = findViewById(R.id.kelimeAnlamEditText)
        kelimeOrnekEdit = findViewById(R.id.kelimeOrnekEditText)
        backButton = findViewById(R.id.ayarlarBackButton)
    }

}