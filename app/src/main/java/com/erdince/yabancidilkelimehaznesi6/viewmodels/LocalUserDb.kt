package com.erdince.yabancidilkelimehaznesi6.viewmodels

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.UserDao
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.WordDao

@Database(entities = [UserModel::class], version = 1)
abstract class LocalUserDb : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: LocalUserDb? = null

        @Synchronized
        fun getInstance(ctx: Context): LocalUserDb {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, LocalUserDb::class.java,
                    "local-user-db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: LocalUserDb) {
            val userDao = db.userDao()

        }
    }

}