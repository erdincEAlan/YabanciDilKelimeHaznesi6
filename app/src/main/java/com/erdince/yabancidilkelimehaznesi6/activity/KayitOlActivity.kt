package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class KayitOlActivity : AppCompatActivity() {
    val db = Firebase.firestore
    var user = Firebase.auth.currentUser
    var girisYapIntent: Intent? = null
    var kayitOlButton: ImageButton? = null
    private var backButton: ImageButton? = null
    var emailEditText: EditText? = null
    var sifreEditText: EditText? = null
    var sifreTekrarEditText: EditText? = null
    var kullaniciAdiEditText: EditText? = null
    var isMailVerify: Boolean? = null
    lateinit var kullaniciAdi: String
    lateinit var email: String
    lateinit var password: String
    lateinit var sifreTekrar: String
    lateinit var uid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kayit_ol)
        init()

    }

    private fun init() {
        initUI()
    }

    private fun initUI() {
        setUI()
        setButtonClickers()
    }


    fun setStringsFromEditTexts() {
        sifreTekrar = sifreTekrarEditText?.text.toString()
        email = emailEditText?.text.toString()
        password = sifreEditText?.text.toString()
        kullaniciAdi = kullaniciAdiEditText?.text.toString()
    }

    fun setUI() {
        backButton = findViewById(R.id.kayitOlBackButton)
        kayitOlButton = findViewById(R.id.kayitOlKayitImageButton)
        emailEditText = findViewById(R.id.kayitOlEmailEditText)
        sifreEditText = findViewById(R.id.kayitOlSifreEditText)
        sifreTekrarEditText = findViewById(R.id.kayitOlSifreTekrarEditText)
        kullaniciAdiEditText = findViewById(R.id.kayitOlKullaniciAdiEditText)
    }

    fun setButtonClickers() {
        kayitOlButton?.setOnClickListener {
            setStringsFromEditTexts()
            signIn()
        }
        backButton?.setOnClickListener {
            switchActivity("GirisYapActivity")
        }
    }


    fun signIn() {
        if (email == "" || password == "" || kullaniciAdi == "") {
            Toast.makeText(
                baseContext, "Lutfen tum alanları doldurdugunuza emin olun",
                Toast.LENGTH_SHORT
            ).show()

        } else if (password != sifreTekrar) {

            Toast.makeText(
                baseContext, "Sifre ve sifre tekrar alanlarının aynı oldugundan emin olun",
                Toast.LENGTH_SHORT
            ).show()

        } else {
            verifyAndSignIn()

        }
    }

    private fun verifyAndSignIn() {
        if (user != null) {
            reloadUser()
            if (isMailVerify == true) {
                updateProfileAndCreateDatabaseDoc()
                startActivity(girisYapIntent)
            } else {
                makeToast("Mail adresiniz doğrulanmamış gözüküyor. Spam klasörünüzü kontrol etmeyi unutmayın. ")
                createEmailVerifyDialog().create().show()
            }
        } else {
            createAccount(email, password)
            createEmailVerifyDialog().create().show()
        }
    }

    private fun updateProfileAndCreateDatabaseDoc() {
        val profileUpdates = userProfileChangeRequest {
            displayName = kullaniciAdi
        }
        user?.updateProfile(profileUpdates)
        reloadUser()
        val kullanici = mutableMapOf(
            "uid" to uid,
            "kullaniciAdi" to kullaniciAdi,
            "kelimeSayisi" to 0,
            "ogrenilenKelimeSayisi" to 0
        )
        db.collection("user").document(uid).set(kullanici)
    }


    private fun createAccount(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    reloadUser()
                }
            }

    }

    private fun createEmailVerifyDialog(): AlertDialog.Builder {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setMessage(R.string.kayit_ol_verify_dialog_msg)
        dialog.setCancelable(false)
        dialog.setPositiveButton(R.string.kayit_ol_verify_dialog_positive) { _, _ ->
            reloadUser()
            verifyAndSignIn()

        }.setNeutralButton(R.string.kayit_ol_verify_dialog_send_link_button) { _, _ ->
            user?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    makeToast("Doğrulama bağlantısı mail adresinize gönderildi. Gönderilen bağlantıya tıklayın ve geri dönün")
                    reloadUser()
                    createEmailVerifyDialog().create().show()
                }
                if (!it.isSuccessful) {
                    user?.delete()
                    user = null
                    makeToast("Doğrulama bağlantısı gönderilemedi, internet bağlantınızı kontrol edin")
                }
            }?.addOnFailureListener() {
                Log.d("FIREBASE ERROR", it.toString())
            }

        }
        return dialog
    }


    fun reloadUser() {
        Firebase.auth.currentUser?.reload()
        user = Firebase.auth.currentUser
        if (user != null) {
            isMailVerify = user!!.isEmailVerified
            uid = user!!.uid
        }
    }


}