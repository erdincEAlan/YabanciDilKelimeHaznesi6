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
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
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
        setFirebase()
        reloadUser()
        setFirebase()
        sendVerificationMail()
        verifyButton.setOnClickListener {
            Toast.makeText(view?.context, "Mail gönderildi",Toast.LENGTH_LONG).show()
            sendVerificationMail()
        }
        val checkVerifyButton : Button = root.findViewById(R.id.checkVerificationButton)
        checkVerifyButton.setOnClickListener {
            reloadUser()
            if(user.isEmailVerified){
                createDatabaseDoc()
                switchToMainActivity()
            }else {
                warnText.text = "Mail adresiniz doğrulanmamış gözüküyor. Mail adresinize gönderilen bağlantıya tıkladığınızdan emin olup eğer tıkladıysanız biraz daha bekleyip tekrar denemenizi rica ederim"

            }
        }
        return root

    }

    private fun switchToMainActivity() {
        val mainIntent : Intent = Intent(context,AnaEkranActivity::class.java)
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