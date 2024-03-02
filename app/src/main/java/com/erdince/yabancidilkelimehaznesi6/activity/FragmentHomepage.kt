package com.erdince.yabancidilkelimehaznesi6.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.quiz.FragmentQuizSourceSelection
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentHomepageBinding
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizBinding
import com.erdince.yabancidilkelimehaznesi6.util.createAndShowDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentHomepage : MainFragment() {

    private lateinit var __binding : FragmentHomepageBinding
    private val binding get() = __binding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        FirebaseApp.initializeApp(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding= FragmentHomepageBinding.inflate(inflater,container,false)
        init()
        stopProgressBar()
        return binding.root
    }

    private fun init() {

        setButtons()
    }

    private fun setButtons() {
        with(binding){
            profilButon.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentHomepage_to_fragmentProfile)
            }
            testButon.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentHomepage_to_fragmentQuizSourceSelection)
            }
            kelimeEkleButon.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentHomepage_to_fragmentWordAdd)

            }
            listeGoruntuleButon.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentHomepage_to_fragmentWordList)
            }
        }

    }







    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentHomepage().apply {
                arguments = Bundle().apply {

                }
            }
    }
}