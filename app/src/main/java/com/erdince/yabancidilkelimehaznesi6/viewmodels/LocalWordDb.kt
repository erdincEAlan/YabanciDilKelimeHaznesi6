package com.erdince.yabancidilkelimehaznesi6.viewmodels

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.WordDao

@Database(entities = [WordModel::class], version = 2)
abstract class LocalWordDb : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        private var instance: LocalWordDb? = null

        @Synchronized
        fun getInstance(ctx: Context): LocalWordDb {
            if (instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext, LocalWordDb::class.java,
                    "local-word-db"
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

        private fun populateDatabase(db: LocalWordDb) {
            val wordDao = db.wordDao()

        }
    }

}