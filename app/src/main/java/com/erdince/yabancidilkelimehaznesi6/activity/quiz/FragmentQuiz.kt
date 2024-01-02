package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizBinding
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val WORD_SRC_PARAM = "wordSource"


@AndroidEntryPoint
class FragmentQuiz : MainFragment() {

    private var kelimelerRef : CollectionReference?=null
    private var kullaniciRef : CollectionReference?=null
    private var soruKelime: WordModel? = null
    private var cevapKelime: String? = null
    private val wordViewModel : DbWordViewModel by viewModels()
    private var __binding : FragmentQuizBinding?=null
    private val binding get() = __binding
    private var wordSourceType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordSourceType = it.getString(WORD_SRC_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        __binding= FragmentQuizBinding.inflate(inflater,container,false)
        init()
        return binding?.root
    }
    fun init() {
        prapare()
        initUI()
    }

    private fun prapare() {
        takeListAndSetQuestKelime()
    }

    private fun initUI() {
        setButtons()
    }


    private fun takeListAndSetQuestKelime() {
        wordViewModel.observeRandomWord(wordSourceType!!)
        wordViewModel.wordLiveData.observe(viewLifecycleOwner){
            if (it.success){
                if (it.data != null){
                    soruKelime = it.data as WordModel
                    binding?.questionTextView?.text = soruKelime?.wordIt?.capitalize(Locale.getDefault())
                }
                stopProgressBar()
            }else{
                requireActivity().makeToast("Sormak için kelime bulunmadığı veya hepsini öğrendiğiniz için anaekrana yönlendirildi. Kelime Ekle ekranından yeni kelime ekleyebilirsiniz")
            }

        }

    }

    private fun setButtons() {
        with(binding!!){
            backButton.setOnClickListener {
                backToHomepage()
            }
            nextWordButton.setOnClickListener {
                increaseKelimePointAndSwitch()
            }
            answerButton.setOnClickListener {
                takeTheAnswerAndInit()
            }
        }

    }

    private fun takeTheAnswerAndInit() {
        setStringsFromEditTexts()
        if (takeStringsAndMakeReadyToQuestioning(soruKelime?.wordMeaning!!)== takeStringsAndMakeReadyToQuestioning(cevapKelime!!)) {
            correctAnswer()
        } else {
            incorrectAnswer()
        }
    }
    private fun takeStringsAndMakeReadyToQuestioning(text:String): String{
        return text.lowercase().replace("\\p{Punct}|\\s".toRegex(), "")
    }
    private fun incorrectAnswer() {
        val fragmentQuizWrongAnswer = FragmentQuizWrongAnswer.newInstance(soruKelime?.wordId!!,wordSourceType!!)
        changeFragment(fragmentQuizWrongAnswer)
    }

    private fun setStringsFromEditTexts() {
        with(binding!!){
            cevapKelime = answerEditText.text.toString()
        }

    }

    private fun correctAnswer() {
        requireActivity().makeToast("Cevap Doğru")
        increaseKelimePointAndSwitch()
    }

    private fun increaseKelimePointAndSwitch() {
        if (wordSourceType == "customWord"){
            soruKelime?.let { wordViewModel.increaseWordPoint(it) }
        }
        refreshTheFragment()

    }

    private fun refreshTheFragment() {
        val thisFragment = newInstance(wordSourceType!!)
        changeFragment(thisFragment)
    }

    companion object {

        @JvmStatic
        fun newInstance(wordSource: String) =
            FragmentQuiz().apply {
                arguments = Bundle().apply {
                    putString(WORD_SRC_PARAM, wordSource)

                }
            }
    }
}