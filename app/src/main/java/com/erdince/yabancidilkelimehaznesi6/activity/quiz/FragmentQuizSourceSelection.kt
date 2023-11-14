package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizSourceSelectionBinding
import dagger.hilt.android.AndroidEntryPoint
import org.checkerframework.checker.units.qual.A


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class FragmentQuizSourceSelection : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var __binding : FragmentQuizSourceSelectionBinding?=null
    val binding get() = __binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        __binding = FragmentQuizSourceSelectionBinding.inflate(inflater, container,false)
        binding?.quizCustomWordsButton?.setOnClickListener(){
            val customWordsQuizFragment : FragmentQuiz = FragmentQuiz.newInstance("kelimeler")
            (activity as TestActivity).changeFragment(customWordsQuizFragment)
        }
        binding?.quizPreparedWordsButton?.setOnClickListener(){
            val preparedWordsQuizFragment : FragmentQuiz = FragmentQuiz.newInstance("preparedWords")
            (activity as TestActivity).changeFragment(preparedWordsQuizFragment)
        }
        (activity as TestActivity).stopProgressBar()
        return binding?.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentQuizSourceSelection().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}