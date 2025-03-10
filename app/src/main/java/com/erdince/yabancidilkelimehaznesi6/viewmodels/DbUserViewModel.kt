package com.erdince.yabancidilkelimehaznesi6.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DbUserViewModel   @Inject constructor(savedStateHandle: SavedStateHandle?, app : Application): ViewModel() {
    private var userResource : ResourceModel<UserModel> = ResourceModel(false,null)
    private var photoUrlResource : ResourceModel<String> = ResourceModel(false,null)
    var userLiveData = MutableLiveData<ResourceModel<UserModel>>()
    var photoUrlLiveData = MutableLiveData<ResourceModel<String>>()
    val application = app
    private var uid : String = Firebase.auth.uid.toString()
private    var profilePhotoDirectoryRef = FirebaseStorage.getInstance().reference.child("images/$uid.jpg")
private val userDocRef = Firebase.firestore.collection("users").document(uid)
    private val userLocalDbController = LocalUserDb.getInstance(application.baseContext).userDao()
    private fun getUserDataFromCloud(){
        userDocRef.get().addOnSuccessListener {
            userResource.data = it.toObject<UserModel>()
            CoroutineScope(Dispatchers.IO).launch{
                userResource.data?.let { userData ->
                    userLocalDbController.updateUserData(userData)
                }
            }
            userResource.success = true
            userLiveData.postValue(userResource)
        }.addOnFailureListener {
            userResource.success = false
            userLiveData.postValue(userResource)
        }
    }
    fun getUserData(){
        CoroutineScope(Dispatchers.IO).launch{
            userLocalDbController.getUserDataById(uid)?.let {
                userResource.data = it
                userResource.success = true
                userLiveData.postValue(userResource)
                return@launch
            }
            getUserDataFromCloud()
        }
    }

    fun updateUserData(userModel : UserModel?){
        getUserData()
        if (userModel != null) {
            userResource.data?.merge(userModel)
            (userResource.data as UserModel?)?.let {
                userDocRef.set(it)
                userLocalDbController.updateUserData(it)
            }

        }
    }

    fun syncUserDatabases(){
        CoroutineScope(Dispatchers.IO).launch {
            updateUserData(
                userLocalDbController.getUserDataById(uid)
            )
        }

    }

    fun checkIfUserDocExists() : Boolean{
        getUserData()
        return userResource.success
    }
    fun createUserData(email : String, displayName : String,uidNew : String,authMethod : String){
        uid = uidNew
        if (!checkIfUserDocExists()){
            var newUserModel : UserModel = UserModel(
                userName = displayName,
                authMethod = authMethod,
                userId = uid,
                userStatus = true,
                profilePhotoUrl = null,
                learnedWordsCount = 0,
                totalWordCount = 0,
            )
            userDocRef.set(newUserModel)
            CoroutineScope(Dispatchers.IO).launch {
                userLocalDbController.updateUserData(newUserModel)
            }
        }
    }

    fun updateProfilePhoto(fileUri : Uri){
       profilePhotoDirectoryRef.putFile(fileUri)
        var newUserDoc : UserModel? = UserModel(userId = UUID.randomUUID().toString())
        profilePhotoDirectoryRef.downloadUrl.addOnSuccessListener {
            var profileUpdate = userProfileChangeRequest {
                photoUri = it
            }
            newUserDoc?.profilePhotoUrl = it.toString()
            updateUserData(newUserDoc)
            Firebase.auth.currentUser?.updateProfile(profileUpdate)?.addOnSuccessListener {
                Log.d("Firebase Photo Update","COMPLETED")
            }?.addOnFailureListener {
                Log.d("Fiirebase Photo Update", "FAILURE $it")
            }
        }



    }
    fun getProfilePhoto(){
        if (Firebase.auth.currentUser?.photoUrl !=null){
            photoUrlResource.success = true
            photoUrlResource.data = Firebase.auth.currentUser?.photoUrl.toString()
        }else{
            photoUrlResource.success = false
        }
        photoUrlLiveData.postValue(photoUrlResource)
    }
}