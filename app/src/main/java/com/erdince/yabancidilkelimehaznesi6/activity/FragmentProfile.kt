package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentProfileBinding
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.UserModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbUserViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentProfile : MainFragment() {
    private lateinit var fragmentProfileBinding: FragmentProfileBinding
    private val binding get() = fragmentProfileBinding
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
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        observeUserData()
    }

    private fun observeUserData() {
        dbUserViewModel.photoUrlLiveData?.observe(viewLifecycleOwner, ::setTheProfilePhoto)
        dbUserViewModel.userLiveData?.observe(viewLifecycleOwner, ::handleUserData)
        dbUserViewModel.getUserData()
        dbUserViewModel.getProfilePhoto()

    }

    private fun setTheProfilePhoto(photoUrlResource: ResourceModel<String>) {
        if (photoUrlResource.success) {
            photoUrlResource.data.let { url ->
                Picasso.get().load(url).into(binding.profilPhotoImageView)
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
            goBack()
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