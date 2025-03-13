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
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.quiz.FragmentQuizSourceSelection
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentHomepageBinding
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizBinding
import com.erdince.yabancidilkelimehaznesi6.util.createAndShowDialog
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.WordDao
import com.erdince.yabancidilkelimehaznesi6.util.isOnline
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.LocalWordDb
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


@AndroidEntryPoint
class FragmentHomepage : MainFragment() {

    private var binding: FragmentHomepageBinding? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        FirebaseApp.initializeApp(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomepageBinding.inflate(inflater, container, false)
        init()
        stopProgressBar()
        return binding?.root
    }

    private fun init() {
        binding?.offlineModeLayout?.isVisible = !requireActivity().isOnline(requireContext())
        setButtons()
    }

    private fun setButtons() {
        binding?.apply {
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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