package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizSourceSelectionBinding
import com.erdince.yabancidilkelimehaznesi6.util.Keys
import com.erdince.yabancidilkelimehaznesi6.util.WordType
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentQuizSourceSelection : MainFragment() {
    private var __binding : FragmentQuizSourceSelectionBinding?=null
    private val binding get() = __binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        __binding = FragmentQuizSourceSelectionBinding.inflate(inflater, container,false)
        binding?.backButton?.setOnClickListener(){
            findNavController().navigateUp()
        }
        binding?.quizCustomWordsButton?.setOnClickListener(){
            findNavController().navigate(
                R.id.action_fragmentQuizSourceSelection_to_fragmentQuiz, bundleOf(
                    Pair(Keys.WordTypeKey.key, WordType.CustomWord.value)
                )
            )
        }
        binding?.quizPreparedWordsButton?.setOnClickListener(){
            findNavController().navigate(
                R.id.action_fragmentQuizSourceSelection_to_fragmentQuiz, bundleOf(
                    Pair(Keys.WordTypeKey.key, WordType.PreparedWord.value)
                )
            )
        }
        stopProgressBar()
        return binding?.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentQuizSourceSelection().apply {
                arguments = Bundle().apply {

                }
            }
    }
}