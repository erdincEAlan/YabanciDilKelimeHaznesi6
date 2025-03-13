package com.erdince.yabancidilkelimehaznesi6.util.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.erdince.yabancidilkelimehaznesi6.activity.MainActivity
import com.erdince.yabancidilkelimehaznesi6.activity.quiz.FragmentQuiz
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.WordType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Dao
interface WordDao {

    @Query("SELECT * FROM wordmodel")
    fun getAll(): List<WordModel>

    @Query("SELECT * FROM wordmodel WHERE wordId IN (:wordIds)")
    fun loadAllByIds(wordIds: IntArray): List<WordModel>

    @Query("SELECT * FROM wordmodel WHERE wordIt LIKE :wordIt  LIMIT 1")
    fun findByName(wordIt: String): WordModel

    fun getWordList(type: String, learnedStatus : Boolean? = null): List<WordModel>{
        learnedStatus?.let {
            return getLearnedStatusCheckedWordList(learnedStatus)
        }
        return getWordListByType(type)
    }

    @Query("Select * FROM wordmodel WHERE wordType = :type")
    fun getWordListByType(type : String) : List<WordModel>

    @Query("Select * FROM wordmodel WHERE wordLearningStatus = :learnedStatus")
    fun getLearnedStatusCheckedWordList(learnedStatus: Boolean) : List<WordModel>

    @Query("SELECT * FROM wordmodel WHERE wordId = :wordId LIMIT 1")
    fun getWordById(wordId: String): WordModel?

    @Query("SELECT * FROM wordmodel WHERE wordLearningStatus = false AND wordId !=:lastWordId  ORDER BY RANDOM() LIMIT 1")
    fun getQuizWord(lastWordId : String) : WordModel?

    @Insert
    fun insertAll(vararg words: WordModel)

    @Delete
    fun delete(word: WordModel)

    fun updateWord(word : WordModel){
        getWordById(word.wordId)?.let { delete(it) }
        insertAll(word)
    }
}