package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizWrongAnswerBinding
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.Keys
import com.erdince.yabancidilkelimehaznesi6.util.WordType
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val PARAM_WORD_ID : String = "wordId"
private const val PARAM_QUIZ_TYPE : String = "quizType"

@AndroidEntryPoint
class FragmentQuizWrongAnswer : MainFragment() {
    var publicWord : WordModel? =null
    private val wordViewModel : DbWordViewModel by activityViewModels()
    var __binding : FragmentQuizWrongAnswerBinding?=null
    val binding get() = __binding!!
    private var wordId: String? = null
    private var quizType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordId = it.getString(Keys.WordIdKey.key)
            quizType = it.getString(Keys.WordTypeKey.key)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding = FragmentQuizWrongAnswerBinding.inflate(inflater,container,false)
        init()
        return binding.root
    }

    fun init() {
        initUI()
        observeData()
    }

    private fun observeData() {
        lifecycleScope.launch {
            wordViewModel.quizWord.collect{ wordResource ->
                if (wordResource != null){
                    publicWord = wordResource.data
                    binding.addToMyCustomWords.isVisible = publicWord?.wordType == WordType.PreparedWord.value
                    initTextViews()
                    stopProgressBar()
                }else{
                    navigateToNewQuiz()
                }
            }
        }
    }

    private fun initUI() {
        setButtonClickers()
    }

    private fun setButtonClickers() {
        with(binding){
            quizResultBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
            nextWordButton.setOnClickListener {
                navigateToNewQuiz()
            }

            addToMyCustomWords.setOnClickListener(){
                publicWord.let {
                    wordViewModel.addPublicWordToCustomWord(it!!)
                }
                addToMyCustomWords.disableButton()
            }
        }

    }

    private fun navigateToNewQuiz() {
        findNavController().navigate(
            R.id.fragmentQuiz, bundleOf(
                Pair(Keys.WordTypeKey.key, quizType),
                Pair(Keys.PreviousWordKey.key, publicWord?.wordId)
            )
        )
    }


    private fun initTextViews() {
        with(binding){
            publicWord?.apply {
                wordTextView.text = wordIt
                wordMeaningTextView.text = wordMeaning
                if (wordExample?.isNotEmpty() == true)
                wordExampleTextView.apply {
                    text =wordExample
                    isVisible = true
                    titleExampleTxt.isVisible = true
                }
            }

        }

    }

    companion object {
        @JvmStatic
        fun newInstance(wrongAnswerWordId: String, wrongAnswerQuizType: String) =
            FragmentQuizWrongAnswer().apply {
                arguments = Bundle().apply {
                    putString(PARAM_WORD_ID, wrongAnswerWordId)
                    putString(PARAM_QUIZ_TYPE,wrongAnswerQuizType)

                }
            }
    }
}