package com.erdince.yabancidilkelimehaznesi6

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.WordDao
import com.erdince.yabancidilkelimehaznesi6.viewmodels.LocalWordDb
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@HiltAndroidApp
class YDKHApp:Application() {
    private var localDbController: WordDao? = null
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        setLocalDb()
    }

    private fun setLocalDb() {
        CoroutineScope(Dispatchers.IO).launch {
            localDbController = LocalWordDb.getInstance(applicationContext).wordDao()
        }
    }

    fun returnLocalDbController(): WordDao? {
        return localDbController
    }


}