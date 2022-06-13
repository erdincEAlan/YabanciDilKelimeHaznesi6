package com.erdince.yabancidilkelimehaznesi6

import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity

class YDKHApp:AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        switchActivity("SplashActivity")
    }

}