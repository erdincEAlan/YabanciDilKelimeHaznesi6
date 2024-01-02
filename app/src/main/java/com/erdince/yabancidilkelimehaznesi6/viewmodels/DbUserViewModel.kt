package com.erdince.yabancidilkelimehaznesi6.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DbUserViewModel   @Inject constructor(savedStateHandle: SavedStateHandle?): ViewModel() {
    private var userResource : ResourceModel<UserModel> = ResourceModel(false,null)
    private var photoUrlResource : ResourceModel<String> = ResourceModel(false,null)
    var userLiveData = MutableLiveData<ResourceModel<UserModel>>()
    var photoUrlLiveData = MutableLiveData<ResourceModel<String>>()
    private var uid : String = Firebase.auth.uid.toString()
private    var profilePhotoDirectoryRef = FirebaseStorage.getInstance().reference.child("images/$uid.jpg")
private val userDocRef = Firebase.firestore.collection("users").document(uid)
    fun getUserData(){
        userDocRef.get().addOnSuccessListener {
            userResource.data = it.toObject<UserModel>()
            userResource.success = true
            userLiveData.postValue(userResource)
        }.addOnFailureListener {
            userResource.success = false
            userLiveData.postValue(userResource)
        }
    }
    fun updateUserData(userModel : UserModel?){
        getUserData()
        if (userModel != null) {
            userResource.data?.merge(userModel)
            userDocRef.set(userResource.data as UserModel)
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
        }

    }

    fun updateProfilePhoto(fileUri : Uri){
       profilePhotoDirectoryRef.putFile(fileUri)
        var newUserDoc : UserModel? = UserModel()
        profilePhotoDirectoryRef.downloadUrl.addOnSuccessListener {
            var profileUpdate = userProfileChangeRequest {
                photoUri = it
                newUserDoc?.profilePhotoUrl = it.toString()
                updateUserData(newUserDoc)
            }
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