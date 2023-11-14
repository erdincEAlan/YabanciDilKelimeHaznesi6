package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizWrongAnswerBinding
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint


private const val PARAM_WORD_ID : String = "wordId"
private const val PARAM_QUIZ_TYPE : String = "quizType"

@AndroidEntryPoint
class FragmentQuizWrongAnswer : Fragment() {
    var publicWord : KelimeModel? =null
    private var preparedWordsRef: CollectionReference? = null
    private val wordViewModel : DbWordViewModel by viewModels()
    var db : FirebaseFirestore?=null
    private var publicToCustomWord :KelimeModel?=null
    private var kelimeKendi : String? = null
    private var kelimeAnlam : String? = null
    private var kelimeOrnek : String? = null
    private var  kelimelerRef : CollectionReference? = null
    var __binding : FragmentQuizWrongAnswerBinding?=null
    val binding get() = __binding!!
    private var wordId: String? = null
    private var quizType: String? = null

    var worddd: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordId = it.getString(PARAM_WORD_ID)
            quizType= it.getString(PARAM_QUIZ_TYPE)

        }
        (requireActivity() as TestActivity).startProgressBar()

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
        wordViewModel.wordLiveData.observe(viewLifecycleOwner){publicToCustomWord ->
            publicWord = publicToCustomWord
            initTextViews()
            (requireActivity() as TestActivity).stopProgressBar()
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
    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.quizFragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
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