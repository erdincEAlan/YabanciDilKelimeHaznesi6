package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentSettingsBinding
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbUserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlin.math.sign

@AndroidEntryPoint
class FragmentSettings : MainFragment() {
private lateinit var fragmentSettingsBinding : FragmentSettingsBinding
    private val binding get() = fragmentSettingsBinding
    private val dbUserViewModel: DbUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater,container,false)
        init()
        return binding.root
    }
    private fun init(){
        observeData()
        setButtons()
    }
    private fun observeData(){
        dbUserViewModel.getUserData()
        dbUserViewModel.getProfilePhoto()
        dbUserViewModel.userLiveData.observe(viewLifecycleOwner, ::handleUserData)
        dbUserViewModel.photoUrlLiveData.observe(viewLifecycleOwner){
            if (it.success){
                Glide.with(requireContext()).load(it.data).centerCrop().circleCrop().into(binding.ayarlarProfilePhoto)
                stopProgressBar()
            }
        }
    }
    private fun handleUserData(userDataResource : ResourceModel<UserModel>){
        if(userDataResource.success){
            userDataResource.data.let {userData ->
                binding.newUsernameEditText.setText(userData?.userName)
            }

        }
    }
    private fun setButtons(){
        with(binding){
            fileButton.setOnClickListener(){
                pickAndUploadProfilePhoto()
            }
            saveButton.setOnClickListener(){
                updateTheUsername()
                backToHomepage()
            }
            backButton.setOnClickListener(){
                goBack()
            }
            logOutButton.setOnClickListener(){
                signOut()
            }
        }
    }
    private fun updateTheUsername(){
        dbUserViewModel.updateUserData(UserModel(userName = binding.newUsernameEditText.text.toString()))
        restartFragment(this)
    }
    private fun pickAndUploadProfilePhoto() {
        TedImagePicker.with(requireContext()).start { uri ->
            dbUserViewModel.updateProfilePhoto(uri)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            FragmentSettings().apply {
                arguments = Bundle().apply {
                }
            }
    }
}