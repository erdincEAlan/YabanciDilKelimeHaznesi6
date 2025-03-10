package com.erdince.yabancidilkelimehaznesi6

import android.app.Application
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.UserDao
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.WordDao
import com.erdince.yabancidilkelimehaznesi6.viewmodels.LocalUserDb
import com.erdince.yabancidilkelimehaznesi6.viewmodels.LocalWordDb
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@HiltAndroidApp
class YDKHApp:Application() {
    private var localWordDbController: WordDao? = null
    private var localUserDbController : UserDao?=null
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        setLocalDb()
    }

    private fun setLocalDb() {
        CoroutineScope(Dispatchers.IO).launch {
            localWordDbController = LocalWordDb.getInstance(applicationContext).wordDao()
            localUserDbController = LocalUserDb.getInstance(applicationContext).userDao()
        }
    }

    fun returnLocalWordDbController(): WordDao? {
        return localWordDbController
    }
    fun returnLocalUserDbController(): UserDao? {
        return localUserDbController
    }


}