package com.erdince.yabancidilkelimehaznesi6.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.erdince.yabancidilkelimehaznesi6.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EmailVerifyFragment : Fragment() {
    private lateinit var db : FirebaseFirestore
    private lateinit var user : FirebaseUser
    private lateinit var databaseUsersReference : CollectionReference

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_email_verify, container, false)
        val warnText : TextView = root.findViewById(R.id.mailVerificationWarningText)
        val verifyButton: Button = root.findViewById(R.id.sendVerificationMailButton)
        val checkVerifyButton : Button = root.findViewById(R.id.checkVerificationButton)
        initButtons(verifyButton, checkVerifyButton, warnText)
        return root

    }

    private fun initButtons(
        verifyButton: Button,
        checkVerifyButton: Button,
        warnText: TextView
    ) {
        verifyButton.setOnClickListener {
            sendVerificationMail()
        }

        checkVerifyButton.setOnClickListener {
            checkVerification(warnText)
        }
    }

    private fun checkVerification(warnText: TextView) {
        reloadUser()
        if (user.isEmailVerified) {
            createDatabaseDoc()
            switchToMainActivity()
        } else {
            warnText.text = getString(R.string.mail_verify_warning_string)

        }
    }

    private fun switchToMainActivity() {
        val mainIntent = Intent(context,AnaEkranActivity::class.java)
        startActivity(mainIntent)
    }

    private fun sendVerificationMail(){
        user.sendEmailVerification().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context,"(Spam klasörünü kontrol etmeyi unutmayın)Doğrulama bağlantısı mail adresinize gönderildi. Gönderilen bağlantıya tıklayın ve geri dönün",Toast.LENGTH_LONG).show()

            }
            if (!it.isSuccessful) {
                user.delete()
                Toast.makeText(context,"Doğrulama bağlantısı gönderilemedi, internet bağlantınızı kontrol edin",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context,"Girilen mail adresi sistemimizde hali hazırda kaydı bulunmakta.",Toast.LENGTH_LONG).show()
        }

    }

    override fun onStart() {
        super.onStart()
        setFirebase()
        sendVerificationMail()
    }
    private fun reloadUser() {
        user.reload()
        user =Firebase.auth.currentUser!!

    }
    private fun setFirebase(){
        db = Firebase.firestore
        user = Firebase.auth.currentUser!!
        setDocumentReferences()
    }
    private fun setDocumentReferences(){
        databaseUsersReference = db.collection("user")
    }
    private fun createDatabaseDoc() {
        val kullanici = mutableMapOf(
            "uid" to user.uid,
            "kullaniciAdi" to user.displayName,
            "kelimeSayisi" to 0,
            "ogrenilenKelimeSayisi" to 0,
            "accountType" to "appAccount"
        )
        databaseUsersReference.document(user.uid).set(kullanici)
    }


}