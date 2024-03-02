package com.erdince.yabancidilkelimehaznesi6

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class YDKHApp:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
    }



}