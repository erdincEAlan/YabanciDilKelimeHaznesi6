package com.erdince.yabancidilkelimehaznesi6.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserModel(
    @PrimaryKey var userId : String = "",
    @ColumnInfo var userStatus : Boolean? = null,
    @ColumnInfo var userName : String? = null,
    @ColumnInfo var totalWordCount : Int? = null,
    @ColumnInfo var learnedWordsCount : Int? = null,
    @ColumnInfo var profilePhotoUrl : String? = null,
    @ColumnInfo var authMethod : String? = null
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
