package com.erdince.yabancidilkelimehaznesi6.util.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.WordType

@Dao
interface WordDao {
    @Query("SELECT * FROM wordmodel")
    fun getAll(): List<WordModel>

    @Query("SELECT * FROM wordmodel WHERE wordId IN (:wordIds)")
    fun loadAllByIds(wordIds: IntArray): List<WordModel>

    @Query("SELECT * FROM wordmodel WHERE wordIt LIKE :wordIt  LIMIT 1")
    fun findByName(wordIt: String): WordModel

    @Query("Select * FROM wordmodel WHERE wordType = :type")
    fun getWordList(type: String): List<WordModel>

    @Query("SELECT * FROM wordmodel WHERE wordId = :wordId LIMIT 1")
    fun getWordById(wordId: String): WordModel?

    @Query("SELECT * FROM wordmodel WHERE wordLearningStatus = false ORDER BY RANDOM() LIMIT 1")
    fun getQuizWord() : WordModel?

    @Insert
    fun insertAll(vararg words: WordModel)

    @Delete
    fun delete(word: WordModel)

    fun updateWord(word : WordModel){
        getWordById(word.wordId)?.let { delete(it) }
        insertAll(word)
    }
}