package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizWrongAnswerBinding
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint


private const val PARAM_WORD_ID : String = "wordId"
private const val PARAM_QUIZ_TYPE : String = "quizType"

@AndroidEntryPoint
class FragmentQuizWrongAnswer : MainFragment() {
    var publicWord : KelimeModel? =null
    private val wordViewModel : DbWordViewModel by viewModels()
    var db : FirebaseFirestore?=null
    var __binding : FragmentQuizWrongAnswerBinding?=null
    val binding get() = __binding!!
    private var wordId: String? = null
    private var quizType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordId = it.getString(PARAM_WORD_ID)
            quizType= it.getString(PARAM_QUIZ_TYPE)

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
            publicWord = resource.data as KelimeModel
            initTextViews()
            stopProgressBar()
        }
        wordViewModel.getWordFromId(wordId,quizType.toString())
    }

    private fun initUI() {
        setButtonClickers()
    }

    private fun setButtonClickers() {
        with(binding){
            quizResultBackButton.setOnClickListener {
                requireActivity().switchActivity("AnaEkranActivity")
            }
            sonrakiKelimeButton.setOnClickListener {
                val quizFragment : FragmentQuiz = FragmentQuiz.newInstance(quizType.toString())
                changeFragment(quizFragment)
            }
            addToMyCustomWords.setOnClickListener(){
                publicWord.let {
                    wordViewModel.addCustomWord(it!!)
                }
                it.isClickable = false
                it.alpha = 0.5f
            }
        }

    }


    private fun initTextViews() {
        with(binding){
            kelimeKendiTextView.text = publicWord?.kelimeKendi
            kelimeAnlamTextView.text = publicWord?.kelimeAnlam
            kelimeOrnekCumleTextView.text = publicWord?.kelimeOrnekCumle
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