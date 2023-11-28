package com.erdince.yabancidilkelimehaznesi6.model

data class WordModel(
    var kelimeID: String? = null, var kelimeKendi: String? = null, var kelimeAnlam: String? = null,
    var kelimeOrnekCumle: String? = null, var kelimeDurum: Int? = null, var kelimeOgrenmeDurum: Int? = null,
    var kelimeSahipID: String? = null, var kelimePuan: Int? = 0, var wordType : String?=null
)