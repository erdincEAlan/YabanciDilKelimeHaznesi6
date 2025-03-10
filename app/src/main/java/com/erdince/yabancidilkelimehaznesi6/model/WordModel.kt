package com.erdince.yabancidilkelimehaznesi6.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordModel(
    @PrimaryKey var wordId: String = "",
    @ColumnInfo var wordIt: String? = null,
    @ColumnInfo var wordMeaning: String? = null,
    @ColumnInfo var wordExample: String? = null,
    @ColumnInfo var wordStatus: Boolean? = null,
    @ColumnInfo var wordLearningStatus: Boolean? = null,
    @ColumnInfo var wordOwnerId: String? = null,
    @ColumnInfo var wordPoint: Int? = 0,
    @ColumnInfo var wordType: String? = null
) {
    fun merge(newWord: WordModel) {
        this.wordId = newWord.wordId ?: this.wordId
        this.wordIt = newWord.wordIt ?: this.wordIt
        this.wordMeaning = newWord.wordMeaning ?: this.wordMeaning
        this.wordExample = newWord.wordExample ?: this.wordExample
        this.wordStatus = newWord.wordStatus ?: this.wordStatus
        this.wordLearningStatus = newWord.wordLearningStatus ?: this.wordLearningStatus
        this.wordOwnerId = newWord.wordOwnerId ?: this.wordOwnerId
        this.wordPoint = newWord.wordPoint ?: this.wordPoint
        this.wordType = newWord.wordType ?: this.wordType
    }

}
