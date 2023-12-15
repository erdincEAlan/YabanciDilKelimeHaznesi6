package com.erdince.yabancidilkelimehaznesi6.model

data class UserModel(
    var userId : String? = null,
    var userStatus : Boolean? = null,
    var userName : String? = null,
    var totalWordCount : Int? = null,
    var learnedWordsCount : Int? = null,
    var pPhotoUrl : String? = null,
    var authMethod : String? = null
)
