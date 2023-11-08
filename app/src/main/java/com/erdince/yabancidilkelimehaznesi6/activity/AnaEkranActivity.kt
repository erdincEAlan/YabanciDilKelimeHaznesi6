package com.erdince.yabancidilkelimehaznesi6.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.createAndShowDialog
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AnaEkranActivity : AppCompatActivity() {

    private var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private lateinit var uid: String
    private var profilButton: ImageButton? = null
    private var kelimeEkleButton: ImageButton? = null
    private var listeGoruntuleButon: ImageButton? = null
    private var testButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anaekran)
        init()
    }



    private fun init() {
        setFirebase()
        userDatabaseDocumentControl()
        setUI()
        setButtons()
    }

    private fun setUI() {

        profilButton = findViewById(R.id.profilButon)
        kelimeEkleButton = findViewById(R.id.kelimeEkleButon)
        listeGoruntuleButon = findViewById(R.id.listeGoruntuleButon)
        testButton = findViewById(R.id.testButon)

    }


    private fun setButtons() {
        profilButton?.setOnClickListener {
            switchActivity("ProfilActivity")
        }
        testButton?.setOnClickListener {
            switchActivity("TestActivity")
        }

        kelimeEkleButton?.setOnClickListener {
            switchActivity("KelimeEkleActivity")
        }
        listeGoruntuleButon?.setOnClickListener {
            switchActivity("KelimeListeActivity")
        }
    }



    private fun userDatabaseDocumentControl() {
        db?.collection("user")?.document(uid)?.get()?.addOnSuccessListener { document ->
            if (!document.exists()) {
                val kullanici = mutableMapOf(
                    "uid" to uid,
                    "kullaniciAdi" to user?.displayName,
                    "kelimeSayisi" to 0,
                    "ogrenilenKelimeSayisi" to 0,
                    "dogrulamaTuru" to "google"
                )
                db?.collection("user")?.document(uid)?.set(kullanici)
            }
        }
    }


    private fun setFirebase() {

        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user!!.uid

    }

    @Deprecated("Deprecated in Java")
    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        createAndShowDialog("Uygulamadan çıkmak mı istiyorsunuz?"){answer->
            if (answer){
                this.finishAffinity()
            }
        }
    }

}