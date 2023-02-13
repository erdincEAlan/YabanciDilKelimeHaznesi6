package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class KayitOlActivity : AppCompatActivity() {

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
    private lateinit var timer: CountDownTimer
    private var cycle : Short = 0
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
            checkAndSignUp()

        }
        backButton?.setOnClickListener {
            switchActivity("GirisYapActivity")
        }
    }

    private fun checkAndSignUp() {
        if(isTextsOk()) {
            verifyAndSignUp()
        }
    }


    private fun isTextsOk() : Boolean {
        if (email == "" || password == "" || kullaniciAdi == "") {
            makeToast("Lütfen tüm alanları doldurduğunuza emin olun")
            return false
        } else if (password != sifreTekrar) {
            return false
            makeToast("Şifre ve şifre tekrar alanlarının aynı olduğuna emin olun")
        } else {
            return true


        }
    }

    private fun verifyAndSignUp() {
        if (user != null) {
            if (isMailVerify == true) {
                switchActivity("AnaEkranActivity")
            } else {
                updateProfile()
                changeFragment(EmailVerifyFragment())
            }
        } else {
            setTimer()
            createAccount(email, password)
            waitForAccountToBeGetCreated()
            reloadUser()
            verifyAndSignUp()
        }
    }

    private fun waitForAccountToBeGetCreated() {
        while (!isAccountCreated()) {
            if (cycle.equals(2)) {
                cycle = 0
                createAccount(email, password)
            }
            makeToast("Lutfen Bekleyin")
            timer.start()

            cycle++

        }
    }

    private fun isAccountCreated() : Boolean{
        reloadUser()
        if (user!=null){
            return true
        }else{
            return false
        }

    }



    private fun updateProfile() {
        val profileUpdates = userProfileChangeRequest {
            displayName = kullaniciAdi
        }
        user?.updateProfile(profileUpdates)
        reloadUser()
    }


    private fun createAccount(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    reloadUser()
                }
            }

    }

    private fun setTimer(){
       timer = object: CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                makeToast("Lütfen Bekleyin")
            }
            override fun onFinish() {


            }
        }


    }

    override fun onStart() {
        super.onStart()
        setFirebase()
    }
    private fun reloadUser() {
        Firebase.auth.currentUser?.reload()
        user = Firebase.auth.currentUser
        user?.reload()
    }

    private fun setFirebase(){
        db = Firebase.firestore
        user = Firebase.auth.currentUser
    }


    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.emailVerifyLayout, fragment)
        fragmentTransaction.commit()
    }

}