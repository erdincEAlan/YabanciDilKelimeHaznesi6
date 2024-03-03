package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.input.key.Key
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizWrongAnswerBinding
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.Keys
import com.erdince.yabancidilkelimehaznesi6.util.WordType
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint


private const val PARAM_WORD_ID : String = "wordId"
private const val PARAM_QUIZ_TYPE : String = "quizType"

@AndroidEntryPoint
class FragmentQuizWrongAnswer : MainFragment() {
    var publicWord : WordModel? =null
    private val wordViewModel : DbWordViewModel by viewModels()
    var db : FirebaseFirestore?=null
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
        observeData(wordId.toString())
    }

    private fun observeData(wordId: String) {
        wordViewModel.wordLiveData.observe(viewLifecycleOwner){resource ->
            if (resource.success){
                publicWord = resource.data as WordModel
                binding.addToMyCustomWords.isVisible = publicWord?.wordType == "preparedWord"
                initTextViews()
                stopProgressBar()
            }else{
                makeToast("Bir sorun oluştu, internet bağlantınızı kontrol edin")
                findNavController().navigateUp()
                stopProgressBar()
            }
        }
        wordViewModel.getWordFromId(wordId,quizType.toString())
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
                findNavController().navigate(
                    R.id.fragmentQuiz, bundleOf(
                        Pair(Keys.WordTypeKey.key, publicWord?.wordType),
                        Pair(Keys.PreviousWordKey.key, publicWord?.wordId)
                    )
                )
            }

            addToMyCustomWords.setOnClickListener(){
                publicWord.let {
                    wordViewModel.addPublicWordToCustomWord(it!!)
                }
                addToMyCustomWords.disableButton()
            }
        }

    }


    private fun initTextViews() {
        with(binding){
            kelimeKendiTextView.text = publicWord?.wordIt
            kelimeAnlamTextView.text = publicWord?.wordMeaning
            kelimeOrnekCumleTextView.text = publicWord?.wordExample
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