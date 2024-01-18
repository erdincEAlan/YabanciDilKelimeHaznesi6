package com.erdince.yabancidilkelimehaznesi6.model

data class WordModel(
    var wordId: String? = null, var wordIt: String? = null, var wordMeaning: String? = null,
    var wordExample: String? = null, var wordStatus: Boolean? = null, var wordLearningStatus: Boolean? = null,
    var wordOwnerId: String? = null, var wordPoint: Int? = 0, var wordType : String?=null
)
