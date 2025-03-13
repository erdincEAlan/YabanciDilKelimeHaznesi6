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
    private var binding: FragmentQuizSourceSelectionBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizSourceSelectionBinding.inflate(inflater, container, false)
        handleButtons()
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        stopProgressBar()
    }

    private fun handleButtons() {
        binding?.apply {
            backButton.setOnClickListener() {
                findNavController().navigateUp()
            }
            quizCustomWordsButton.setOnClickListener() {
                findNavController().navigate(
                    R.id.action_fragmentQuizSourceSelection_to_fragmentQuiz, bundleOf(
                        Pair(Keys.WordTypeKey.key, WordType.CustomWord.value)
                    )
                )
            }
            quizPreparedWordsButton.setOnClickListener() {
                findNavController().navigate(
                    R.id.action_fragmentQuizSourceSelection_to_fragmentQuiz, bundleOf(
                        Pair(Keys.WordTypeKey.key, WordType.PreparedWord.value)
                    )
                )
            }
        }
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