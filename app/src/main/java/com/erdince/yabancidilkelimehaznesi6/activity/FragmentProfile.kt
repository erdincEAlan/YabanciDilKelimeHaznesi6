package com.erdince.yabancidilkelimehaznesi6.activity

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentLearnedWordsBinding
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentProfileBinding
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.erdince.yabancidilkelimehaznesi6.util.setVisibilityWithAnimation

@AndroidEntryPoint
class FragmentProfile : MainFragment() {
    private lateinit var fragmentProfileBinding: FragmentProfileBinding
    private val binding get() = fragmentProfileBinding
    private val dbUserViewModel: DbUserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setButtons()
        observeUserData()
    }
    private fun setButtons(){
        with(binding){
            settingsButton.setOnClickListener(){
                findNavController().navigate(R.id.action_fragmentProfile_to_fragmentSettings)
            }
            backButton.setOnClickListener(){
                findNavController().navigateUp()
            }
            learnedWordsListButton.setOnClickListener(){
                findNavController().navigate(R.id.action_fragmentProfile_to_learnedWordsFragment)
            }
        }
    }
    private fun observeUserData() {
        dbUserViewModel.getUserData()
        dbUserViewModel.getProfilePhoto()
        dbUserViewModel.photoUrlLiveData.observe(viewLifecycleOwner, ::getTheProfilePhoto)
        dbUserViewModel.userLiveData.observe(viewLifecycleOwner, ::handleUserData)
    }

    private fun getTheProfilePhoto(photoUrlResource: ResourceModel<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            if (photoUrlResource.success) {
                val glideListener = object : RequestListener<Drawable>{
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
                        setImageAndDisableLoading(resource)

                        return false
                    }

                }
                photoUrlResource.data.let { url ->
                    val glide = Glide.with(requireContext()).load(url).listener(glideListener)
                    glide.submit()
                }
            }
        }

    }

    private fun setImageAndDisableLoading(resource: Drawable) {
        CoroutineScope(Dispatchers.Main).launch {
            binding.apply {
                profilPhotoImageView.setImageDrawable(resource)
                progressBar.setVisibilityWithAnimation(false)
                profilPhotoImageView.setVisibilityWithAnimation(true)

            }
        }

    }

    private fun handleUserData(userDataResource: ResourceModel<UserModel>) {
        if (userDataResource.success) {
            with(binding) {
                if (userDataResource.data != null) {
                    userNameTextView.text = userDataResource.data?.userName
                    totalLearnedWordsCountTextView.text = userDataResource.data?.learnedWordsCount.toString()
                    totalWordCountTextView.text = userDataResource.data?.totalWordCount.toString()
                }
            }
            stopProgressBar()
        } else {
            throwDefaultWarning()
            findNavController().navigateUp()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentProfile().apply {
                arguments = Bundle().apply {
                }
            }
    }
}