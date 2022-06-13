package com.erdince.yabancidilkelimehaznesi6.activity


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.util.GoogleServerClientId
import com.erdince.yabancidilkelimehaznesi6.util.SavedPreference
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GirisYapActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val regCode: Int = 123
    val firebaseAuth = FirebaseAuth.getInstance()
    val auth = Firebase.auth
    var gso: GoogleSignInOptions? = null
    var sifremiUnuttumButton: TextView? = null
    var girisYapButton: ImageButton? = null
    var kayitolButton: TextView? = null
    var signInGoogleButon: ImageButton? = null
    var emailEditText: EditText? = null
    var sifreEditText: EditText? = null
    lateinit var girisEmail: String
    lateinit var girisSifre: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giris_yap)
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


    fun initButtons() {

        signInGoogleButon?.setOnClickListener() {
            signInGoogle()
        }

        girisYapButton?.setOnClickListener() {

            takeInfosFromEditTexts()

            if (girisEmail != "" && girisSifre != "") {
                signInWithEmail()
            } else {
                Toast.makeText(
                    baseContext, "Email ve sifrenizi kontrol edin",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }


        kayitolButton?.setOnClickListener() {
            switchActivity("KayitOlActivity")
        }


        sifremiUnuttumButton?.setOnClickListener() {

            switchActivity("SifreSifirlaActivity")
        }

    }

    private fun signInWithEmail() {
        auth.signInWithEmailAndPassword(girisEmail, girisSifre)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    switchActivity("AnaEkranActivity")
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    makeToast("Email ve sifrenizi kontrol edin")

                }
            }
    }

    private fun takeInfosFromEditTexts() {
        girisEmail = emailEditText?.text.toString()
        girisSifre = sifreEditText?.text.toString()
    }

    fun setUI() {

        sifremiUnuttumButton = findViewById(R.id.sifremiUnuttum)
        girisYapButton = findViewById(R.id.girisYapButon)
        kayitolButton = findViewById(R.id.kayitOlButton)
        signInGoogleButon = findViewById(R.id.googleSıgnButton)
        emailEditText = findViewById(R.id.girisYapKullaniciEditText)
        sifreEditText = findViewById(R.id.girisYapSifreEditText)

    }


    private fun signInGoogle() {

        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, regCode)
    }

    // onActivityResult() function : this is where we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == regCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    // handleResult() function -  this is where we update the UI after Google signin takes place
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
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                SavedPreference.setEmail(this, account.email.toString())
                SavedPreference.setUsername(this, account.displayName.toString())
                switchActivity("AnaEkranActivity")
                finish()
            }
        }
    }



}