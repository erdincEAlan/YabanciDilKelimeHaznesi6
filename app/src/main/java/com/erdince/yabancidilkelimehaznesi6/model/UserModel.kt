package com.erdince.yabancidilkelimehaznesi6.model

data class UserModel(
    var userId : String? = null,
    var userStatus : Boolean? = null,
    var userName : String? = null,
    var totalWordCount : Int? = null,
    var learnedWordsCount : Int? = null,
    var profilePhotoUrl : String? = null,
    var authMethod : String? = null
){
    fun merge(newUserData : UserModel){
        this.userId = newUserData.userId ?: this.userId
        this.userStatus = newUserData.userStatus ?: this.userStatus
        this.userName = newUserData.userName ?: this.userName
        this.totalWordCount = newUserData.totalWordCount ?: this.totalWordCount
        this.learnedWordsCount = newUserData.learnedWordsCount ?: this.learnedWordsCount
        this.profilePhotoUrl = newUserData.profilePhotoUrl ?: this.profilePhotoUrl
        this.authMethod = newUserData.authMethod ?: this.authMethod
    }
}
