package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.activity.quiz.FragmentQuizSourceSelection
import com.erdince.yabancidilkelimehaznesi6.util.isOnline
import com.erdince.yabancidilkelimehaznesi6.util.openNetworkSettings
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import com.erdince.yabancidilkelimehaznesi6.util.*
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var db: FirebaseFirestore? = null
    var auth = Firebase.auth
    private var user: FirebaseUser? = null
    lateinit var uid: String
    private var progressBar : LinearLayout?=null
    private var fragmentContainer : FragmentContainerView?=null
    val fragmentManager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        FirebaseApp.initializeApp(applicationContext)
        setFirebase()
        progressBar = findViewById(R.id.progressBar)
        fragmentContainer = findViewById(R.id.quizFragmentContainer)
        setBackPressed()
        stopProgressBar()
    }

    private fun setBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (fragmentManager.backStackEntryCount > 1) {
                    goBack()
                } else {
                    finish()
                }
            }
        })
    }
    fun backToHomepage(){
        startProgressBar()
        supportFragmentManager.popBackStack("", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .add(R.id.quizFragmentContainer, FragmentHomepage.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()

    }
    fun goBack() {
        fragmentManager.popBackStack()
        startProgressBar()
    }

    fun changeFragment(fragment: Fragment) {
         startProgressBar()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.quizFragmentContainer, fragment)
         fragmentTransaction.addToBackStack(null)
         fragmentTransaction.commitAllowingStateLoss()

    }
    fun changeFragmentWithoutLoadingBar(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.quizFragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()

    }
    fun restartFragment(fragment: Fragment){
        startProgressBar()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.quizFragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    fun returnUid() : String{
        return uid
    }
    private fun startProgressBar(){
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
    override fun onStart() {
        super.onStart()
        setNetworkAlert()
        networkCheck()
        setFirebase()
    }



    private fun checkIsSignedInAndSwitchActivity() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            changeFragment(FragmentHomepage.newInstance())
        }else{
            changeFragment(FragmentLogin.newInstance())
        }
    }

    private fun networkCheck() {
        if (isOnline(this)) {
            checkIsSignedInAndSwitchActivity()
        } else {
            showNetworkAlert()
        }
    }

    private fun showNetworkAlert() {
        setNetworkAlert().create().show()
    }

    private fun setNetworkAlert() : AlertDialog.Builder {
        var alert = AlertDialog.Builder(this)
        alert.setMessage(R.string.network_error_main)
        alert.setCancelable(false)
        alert.setPositiveButton(R.string.network_dialog_wifi_button) { _, _ ->
            openNetworkSettings("Wifi")
        }
        alert.setNegativeButton(R.string.network_dialog_mobile_data_button) { _, _ ->
            openNetworkSettings("Mobile")
        }
    return alert
    }

}