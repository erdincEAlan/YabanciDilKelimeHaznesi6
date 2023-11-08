package com.erdince.yabancidilkelimehaznesi6.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfilActivity : AppCompatActivity() {
    private var kullaniciAdiTextView: TextView?=null
    private var profilKelimeSayisiTextView: TextView?=null
    private var profilOgrenilenKelimeSayiTextView: TextView?=null
    private var profilPhoto: ImageView? = null
    private var profilGeriButton: ImageButton? = null
    private var ogrenilenKelimelerButton: Button? = null
    private var profilAyarButton: ImageButton? = null
    private lateinit var kullaniciAdi: String
    private lateinit var kelimeSayi: String
    private lateinit var ogrenilenKelimeSayi: String
    private lateinit var kullanici: MutableMap<String, Any>
    private var db: FirebaseFirestore? = null
    private var user : FirebaseUser? = null
    private var userDatabaseRef: DocumentReference? = null
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        init()

    }

    private fun init() {
        initFirebase()
        initUI()
        takeUserInfoAndSet()
    }

    private fun initUI() {
        setUI()
        setProfilePhoto()
        setButtonClickers()
    }

    private fun setButtonClickers() {
        ogrenilenKelimelerButton?.setOnClickListener {
            switchActivity("OgrenilenKelimelerListActivity")
        }

        profilGeriButton?.setOnClickListener {
            switchActivity("AnaEkranActivity")
        }

        profilAyarButton?.setOnClickListener {
            switchActivity("AyarlarActivity")
        }
    }

    private fun setProfilePhoto() {
        if (photoUri != null) {
            Picasso.get().load(photoUri).resize(450, 350).into(profilPhoto)
        }
    }

    private fun setUI() {
        profilPhoto = findViewById(R.id.profilPhotoImageView)
        profilGeriButton = findViewById(R.id.profilGeriButton)
        ogrenilenKelimelerButton = findViewById(R.id.profilOgrenilenKelimelerButton)
        profilAyarButton = findViewById(R.id.profilAyarlarButton)
        kullaniciAdiTextView = findViewById(R.id.profilKullaniciAdiTxt)
        profilKelimeSayisiTextView = findViewById(R.id.kelimeSayisiTextView)
        profilOgrenilenKelimeSayiTextView = findViewById(R.id.ogrenilenKelimeSayiTextView)

    }


    private fun takeUserInfoAndSet() {
        userDatabaseRef?.get()?.addOnSuccessListener { user ->
            kullanici = user.data as MutableMap<String, Any>
            kullaniciAdi = kullanici["kullaniciAdi"].toString()
            kelimeSayi = kullanici["kelimeSayisi"].toString()
            ogrenilenKelimeSayi = kullanici["ogrenilenKelimeSayisi"].toString()
            setTextViews()
        }
    }

    private fun setTextViews() {
        kullaniciAdiTextView?.text = kullaniciAdi
        profilKelimeSayisiTextView?.text = kelimeSayi
        profilOgrenilenKelimeSayiTextView?.text = ogrenilenKelimeSayi
    }

    private fun initFirebase() {
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        userDatabaseRef = db?.collection("user")?.document(user!!.uid)
        photoUri = user?.photoUrl
    }

}