package com.erdince.yabancidilkelimehaznesi6.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DbUserViewModel   @Inject constructor(savedStateHandle: SavedStateHandle?): ViewModel() {
    private var userResource : ResourceModel<UserModel> = ResourceModel(false,null)
    private var photoUrlResource : ResourceModel<String> = ResourceModel(false,null)
    var userLiveData : MutableLiveData<ResourceModel<UserModel>>? = null
    var photoUrlLiveData : MutableLiveData<ResourceModel<String>>? = null

    private var uid : String = Firebase.auth.uid.toString()
    fun getUserData(){
        Firebase.firestore.collection("users").document(uid).get().addOnSuccessListener {
            userResource.data = it.toObject<UserModel>()
            userResource.success = true
            userLiveData?.postValue(userResource)
        }.addOnFailureListener {
            userResource.success = false
            userLiveData?.postValue(userResource)
        }
        userLiveData?.postValue(userResource)
    }
    fun getProfilePhoto(){
        if (Firebase.auth.currentUser?.photoUrl !=null){
            photoUrlResource.success = true
            photoUrlResource.data = Firebase.auth.currentUser?.photoUrl.toString()
        }else{
            photoUrlResource.success = false
        }
        photoUrlLiveData?.postValue(photoUrlResource)
    }
}