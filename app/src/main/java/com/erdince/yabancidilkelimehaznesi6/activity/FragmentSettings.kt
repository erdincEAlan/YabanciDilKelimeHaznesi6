package com.erdince.yabancidilkelimehaznesi6.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentSettingsBinding
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
import com.erdince.yabancidilkelimehaznesi6.util.setVisibilityWithAnimation
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

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
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            dbUserViewModel.updateProfilePhoto(uri,
                completedCallback = {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {dbUserViewModel.getProfilePhoto()},
                        500
                    )

                })


        } else {
            Log.d("PhotoPicker", "No image selected")
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
                CoroutineScope(Dispatchers.Main).launch {
                    val glideListener = object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            p0: GlideException?,
                            p1: Any?,
                            p2: Target<Drawable>,
                            p3: Boolean
                        ): Boolean {
                            Log.d("Glide","can't load")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            p1: Any,
                            p2: Target<Drawable>?,
                            p3: DataSource,
                            p4: Boolean
                        ): Boolean {
                            makePpVisible(resource)
                            return false
                        }

                    }
                    val glide = Glide.with(requireContext()).load(it.data).centerCrop().circleCrop().listener(glideListener)
                    glide.submit()
                }

            }
        }
    }

    private fun makePpVisible(resource: Drawable) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.ayarlarProfilePhoto.apply {
             setVisibilityWithAnimation(false)
                setImageDrawable(resource)
             setVisibilityWithAnimation(true)
            }
            stopProgressBar()
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
                findNavController().navigateUp()
            }
            logOutButton.setOnClickListener(){
                signOut()
            }
        }
    }
    private fun updateTheUsername(){
        dbUserViewModel.updateUserData(UserModel(userId = UUID.randomUUID().toString(), userName = binding.newUsernameEditText.text.toString()))
        restartFragment(this)
    }
    private fun pickAndUploadProfilePhoto() {
         pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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