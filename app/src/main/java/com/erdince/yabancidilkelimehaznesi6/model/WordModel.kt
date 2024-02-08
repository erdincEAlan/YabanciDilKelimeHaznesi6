package com.erdince.yabancidilkelimehaznesi6.model

data class WordModel(
    var wordId: String? = null, var wordIt: String? = null, var wordMeaning: String? = null,
    var wordExample: String? = null, var wordStatus: Boolean? = null, var wordLearningStatus: Boolean? = null,
    var wordOwnerId: String? = null, var wordPoint: Int? = 0, var wordType : String?=null
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
