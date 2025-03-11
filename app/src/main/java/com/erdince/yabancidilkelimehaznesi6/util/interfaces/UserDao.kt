package com.erdince.yabancidilkelimehaznesi6.util.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
@Dao
interface UserDao {

    @Query("SELECT * FROM usermodel WHERE userId LIKE :uid  LIMIT 1")
    fun getUserDataById(uid: String): UserModel?

    @Insert
    fun addUserData(userModel : UserModel)

    @Delete
    fun delete(userModel: UserModel)

    fun updateUserData(userModel: UserModel){
        userModel.userId.let {
            getUserDataById(it)?.let { it1 -> delete(it1) }
        }
        addUserData(userModel)
    }
}