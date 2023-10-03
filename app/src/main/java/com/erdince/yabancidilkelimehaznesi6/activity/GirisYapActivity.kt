package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.health.UidHealthStats
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.util.GoogleServerClientId
import com.erdince.yabancidilkelimehaznesi6.util.GoogleSavedPreference
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GirisYapActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var signInAttempt : Int = 0
    private val regCode: Int = 123
    private val auth = Firebase.auth
    private var gso: GoogleSignInOptions? = null
    private var forgotMyPassword: TextView? = null
    private var loginButton: ImageButton? = null
    private var signInButton: TextView? = null
    private  var signInGoogleButon: ImageButton? = null
    private  var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private  lateinit var loginEmail: String
    private lateinit var loginPass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()

    }

    private fun init() {

        setUI()
        initGoogleSignInOptions()
        initButtons()
    }

    private fun initGoogleSignInOptions() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

            .requestIdToken(GoogleServerClientId.clientId)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso!!)
    }


    private fun initButtons() {

        signInGoogleButon?.setOnClickListener {
            signInGoogle()
        }

       /* loginButton?.setOnClickListener {

            takeInfosFromEditTexts()

            signInCheck()
        }


        signInButton?.setOnClickListener {
            switchActivity("KayitOlActivity")
        }


        forgotMyPassword?.setOnClickListener {

            switchActivity("SifreSifirlaActivity")
        }
*/
    }
/*
    private fun signInCheck() {
        if (loginEmail != "" && loginPass != "") {
            signInWithEmail()
        } else {
            makeToast("Email ve şifre alanları doldurulmalı")
        }
    }*/
/*
    private fun signInWithEmail() {
        auth.signInWithEmailAndPassword(loginEmail, loginPass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    switchActivity("AnaEkranActivity")
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    makeToast("Email ve sifrenizi kontrol edin")
                    signInAttemptCheck()
                }
            }
    }*/
/*
    private fun signInAttemptCheck() {
        if (signInAttempt==6) {
            makeToast("Şifrenizi mi unuttunuz? Şifrenizi sıfırlamak için \"Şifremi Unuttum\" butonuna basabilirsiniz")
        } else {
            signInAttempt++
        }
    }

    private fun takeInfosFromEditTexts() {
        loginEmail = emailEditText?.text.toString()
        loginPass = passwordEditText?.text.toString()
    }
*/
    fun setUI() {
/*
        forgotMyPassword = findViewById(R.id.sifremiUnuttum)
        loginButton = findViewById(R.id.girisYapButon)
        signInButton = findViewById(R.id.kayitOlButton)
        emailEditText = findViewById(R.id.girisYapKullaniciEditText)
        passwordEditText = findViewById(R.id.girisYapSifreEditText)*/
        signInGoogleButon = findViewById(R.id.googleSıgnButton)


    }


    private fun signInGoogle() {

        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, regCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == regCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }


    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                GoogleSavedPreference.setEmail(this, account.email.toString())
                GoogleSavedPreference.setUsername(this, account.displayName.toString())
                switchActivity("AnaEkranActivity")
                finish()
            }
        }
    }




}