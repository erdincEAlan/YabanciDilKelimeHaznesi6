package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SifreSifirlaActivity : AppCompatActivity() {
    private var girisYapIntent: Intent? = null
    private var backButton: ImageButton? = null
    private var sendMailButton : ImageButton?=null
    private var emailEditText : EditText?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sifre_sifirla)
        init()


    }

    private fun init() {
        initUI()
    }

    private fun initUI() {
        setIntents()
        setUI()
        setButtonClickers()
    }

    private fun sendResetEmail(email: String) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    makeToast("Şifre sıfırlama bağlantınız mail adresinize gönderildi")
                }
                if (!task.isSuccessful){
                    makeToast("Mail gönderilemedi, mail adresinizin doğru olduğundan emin olun.")
                }
            }
    }

    private fun setButtonClickers() {

        backButton?.setOnClickListener {


        }

        sendMailButton?.setOnClickListener{
            sendResetEmail(emailEditText?.text.toString())
        }
    }

    private fun setUI() {
        backButton = findViewById(R.id.sifreSifirlaBackButton)
        sendMailButton = findViewById(R.id.sendPassResetMailButton)
        emailEditText = findViewById(R.id.sifreSifirlaMailEditText)
    }

    private fun setIntents() {


    }
}