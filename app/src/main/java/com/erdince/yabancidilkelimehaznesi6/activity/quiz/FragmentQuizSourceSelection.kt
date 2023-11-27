package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.erdince.yabancidilkelimehaznesi6.activity.MainActivity
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizSourceSelectionBinding
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
        binding?.quizCustomWordsButton?.setOnClickListener(){
            val customWordsQuizFragment : FragmentQuiz = FragmentQuiz.newInstance("kelimeler")
            changeFragment(customWordsQuizFragment)
        }
        binding?.quizPreparedWordsButton?.setOnClickListener(){
            val preparedWordsQuizFragment : FragmentQuiz = FragmentQuiz.newInstance("preparedWords")
            changeFragment(preparedWordsQuizFragment)
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