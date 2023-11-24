package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.activity.quiz.FragmentQuizSourceSelection
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    lateinit var uid: String
    private var progressBar : LinearLayout?=null
    private var fragmentContainer : FragmentContainerView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        FirebaseApp.initializeApp(applicationContext)
        setFirebase()
        progressBar = findViewById(R.id.progressBar)
        fragmentContainer = findViewById(R.id.quizFragmentContainer)
        stopProgressBar()
       // startActivity(Intent(this, SplashActivity::class.java))
        startActivity(Intent(this, SplashActivity::class.java))
    }
     fun changeFragment(fragment: Fragment) {
         startProgressBar()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.quizFragmentContainer, fragment)
        fragmentTransaction.commit()
    }
    fun returnUid() : String{
        return uid
    }
    fun startProgressBar(){
        fragmentContainer?.isVisible = false
        progressBar?.isVisible = true

    }
    fun stopProgressBar(){
        progressBar?.isVisible = false
        fragmentContainer?.isVisible = true
    }

    private fun setFirebase() {
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user?.uid.toString()
    }

}