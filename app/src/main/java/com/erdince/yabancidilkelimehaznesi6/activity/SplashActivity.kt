package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.util.isOnline
import com.erdince.yabancidilkelimehaznesi6.util.openNetworkSettings
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity:AppCompatActivity() {
    private var auth : FirebaseAuth?=null
    private var alert : AlertDialog.Builder?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

    }

    override fun onStart() {
        super.onStart()
        setNetworkAlert()
        networkCheck()
        setFirebase()
    }



    private fun checkIsSignedInAndSwitchActivity() {
        val currentUser = auth?.currentUser
        if (currentUser != null) {
            switchActivity("AnaEkranActivity")
        }else{
            switchActivity("GirisYapActivity")
        }
    }

    private fun setFirebase(){
        auth = Firebase.auth
    }

    private fun networkCheck() {
        if (isOnline(this)) {
            countDownAndSwitchActivity()
        } else {
            showNetworkAlert()
        }
    }

    private fun showNetworkAlert() {
        alert?.create()?.show()
    }

    private fun setNetworkAlert() {
        alert= AlertDialog.Builder(this)
        alert?.setMessage(R.string.network_error_main)
        alert?.setCancelable(false)
        alert?.setPositiveButton(R.string.network_dialog_wifi_button) { _, _ ->
            openNetworkSettings("Wifi")
        }
        alert?.setNegativeButton(R.string.network_dialog_mobile_data_button) { _, _ ->
            openNetworkSettings("Mobile")
        }

    }


    private fun countDownAndSwitchActivity() {
        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                checkIsSignedInAndSwitchActivity()
            }
        }
        timer.start()
    }



}