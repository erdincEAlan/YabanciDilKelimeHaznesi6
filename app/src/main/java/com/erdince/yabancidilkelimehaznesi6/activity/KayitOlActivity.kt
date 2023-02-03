package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class KayitOlActivity : AppCompatActivity() {
    private var databaseUsersReference : CollectionReference?=null
    private var db : FirebaseFirestore?=null
    private var user : FirebaseUser?=null
    private var kayitOlButton: ImageButton? = null
    private var backButton: ImageButton? = null
    private var emailEditText: EditText? = null
    private var sifreEditText: EditText? = null
    private var sifreTekrarEditText: EditText? = null
    private var kullaniciAdiEditText: EditText? = null
    private var isMailVerify: Boolean? = null
    private lateinit var kullaniciAdi: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var sifreTekrar: String
    private lateinit var uid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kayit_ol)
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


    private fun setStringsFromEditTexts() {
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
            reloadUser()
            signUp()
            /*val emailVerifyFragment = emailVerifyFragment()
            changeFragment(emailVerifyFragment)*/
        }
        backButton?.setOnClickListener {
            switchActivity("GirisYapActivity")
        }
    }


    private fun signUp() {
        if (email == "" || password == "" || kullaniciAdi == "") {
            makeToast("Lütfen tüm alanları doldurduğunuza emin olun")

        } else if (password != sifreTekrar) {

            makeToast("Şifre ve şifre tekrar alanlarının aynı olduğuna emin olun")

        } else {
            verifyAndSignUp()

        }
    }

    private fun verifyAndSignUp() {
        if (user != null) {
            if (isMailVerify == true) {
                updateProfileAndCreateDatabaseDoc()
                switchActivity("AnaEkranActivity")
            } else {
                reloadUser()
                changeFragment(emailVerifyFragment())
            }
        } else {
            createAccount(email, password)
            changeFragment(emailVerifyFragment())
        }
    }

    private fun updateProfileAndCreateDatabaseDoc() {
        updateProfile()
        createDatabaseDoc()
    }

    private fun updateProfile() {
        val profileUpdates = userProfileChangeRequest {
            displayName = kullaniciAdi
        }
        user?.updateProfile(profileUpdates)
        reloadUser()
    }

    private fun createDatabaseDoc() {
        val kullanici = mutableMapOf(
            "uid" to uid,
            "kullaniciAdi" to kullaniciAdi,
            "kelimeSayisi" to 0,
            "ogrenilenKelimeSayisi" to 0,
            "accountType" to "appAccount"
        )
        databaseUsersReference?.document(uid)?.set(kullanici)
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
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setMessage(R.string.kayit_ol_verify_dialog_msg)
        alert.setCancelable(true)
        alert.setPositiveButton(R.string.kayit_ol_verify_dialog_positive) { _, _ ->
            reloadUser()
            verifyAndSignUp()
            if (isMailVerify==false){
                makeToast("Mail adresiniz doğrulanmamış gözüküyor")
            }

        }.setNeutralButton(R.string.kayit_ol_verify_dialog_send_link_button) { dialog, _ ->
            user?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    makeToast("(Spam klasörünü kontrol etmeyi unutmayın)Doğrulama bağlantısı mail adresinize gönderildi. Gönderilen bağlantıya tıklayın ve geri dönün")
                    reloadUser()
                    dialog.cancel()
                }
                if (!it.isSuccessful) {
                    user?.delete()
                    user = null
                    makeToast("Doğrulama bağlantısı gönderilemedi, internet bağlantınızı kontrol edin")
                }
            }?.addOnFailureListener {
                makeToast("Girilen mail adresi sistemimizde hali hazırda kaydı bulunmakta.")
            }

        }
        return alert
    }


    private fun reloadUser() {
        Firebase.auth.currentUser?.reload()
        user = Firebase.auth.currentUser
        user?.reload()
        if (user != null) {
            isMailVerify = user!!.isEmailVerified
            uid = user!!.uid
        }
    }

    private fun setFirebase(){
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        setDocumentReferences()
    }
    private fun setDocumentReferences(){
        databaseUsersReference = db?.collection("user")
    }


    fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.emailVerifyLayout, fragment)
        fragmentTransaction.commit()
    }

}