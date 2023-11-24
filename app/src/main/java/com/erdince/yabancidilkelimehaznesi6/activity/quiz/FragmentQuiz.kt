package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.MainActivity
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizBinding
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser
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
    private var soruKelime: KelimeModel? = null
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
        setButtonClickers()
    }


    private fun takeListAndSetQuestKelime() {
        wordViewModel.observeRandomWord(wordSourceType!!)
        wordViewModel.wordLiveData.observe(viewLifecycleOwner){
            if (it.succes){
                if (it.data != null){
                    soruKelime = it.data as KelimeModel
                    binding?.soruKelimeTextView?.text = soruKelime?.kelimeKendi?.capitalize(Locale.getDefault())
                }
                stopProgressBar()
            }else{
                requireActivity().makeToast("Sormak için kelime bulunmadığı veya hepsini öğrendiğiniz için anaekrana yönlendirildi. Kelime Ekle ekranından yeni kelime ekleyebilirsiniz")
                requireActivity().switchActivity("AnaEkranActivity")
            }

        }

    }

    private fun setButtonClickers() {
        with(binding!!){
            backButton.setOnClickListener {
                changeFragment(FragmentQuizSourceSelection.newInstance())
            }
            sonrakiKelimeButton.setOnClickListener {
                increaseKelimePointAndSwitch()
            }
            cevapButton.setOnClickListener {
                takeTheAnswerAndInit()
            }
        }

    }

    private fun takeTheAnswerAndInit() {
        setStringsFromEditTexts()
        if (takeStringsAndMakeReadyToQuestioning(soruKelime?.kelimeAnlam!!)== takeStringsAndMakeReadyToQuestioning(cevapKelime!!)) {
            correctAnswer()
        } else {
            incorrectAnswer()
        }
    }
    private fun takeStringsAndMakeReadyToQuestioning(text:String): String{
        return text.lowercase().replace("\\p{Punct}|\\s".toRegex(), "")
    }
    private fun incorrectAnswer() {
        requireActivity().makeToast("Cevap Yanlış")
        val fragmentQuizWrongAnswer = FragmentQuizWrongAnswer.newInstance(soruKelime?.kelimeID!!,wordSourceType!!)
        changeFragment(fragmentQuizWrongAnswer)
    }

    private fun setStringsFromEditTexts() {
        with(binding!!){
            cevapKelime = cevapKelimeEditText.text.toString()
        }

    }

    private fun correctAnswer() {
        requireActivity().makeToast("Cevap Doğru")
        increaseKelimePointAndSwitch()
    }

    private fun increaseKelimePointAndSwitch() {
        if (wordSourceType == "kelimeler"){
            kelimelerRef?.document(soruKelime?.kelimeID.toString())?.update("kelimePuan", FieldValue.increment(1))
            checkKelimeLearnedAndUpdate()
            requireActivity().makeToast("Kelimeye puan eklendi")
        }
        refreshTheFragment()

    }

    private fun refreshTheFragment() {
        val thisFragment = newInstance(wordSourceType!!)
        changeFragment(thisFragment)
    }


    private fun checkKelimeLearnedAndUpdate() {
        if (soruKelime?.kelimePuan == 6) {
            setKelimeLearned()
            updateUserOgrenilenKelimeNumber()
        }
    }

    private fun setKelimeLearned() {
        kelimelerRef?.document(soruKelime?.kelimeID.toString())?.update("kelimeOgrenmeDurum", 1)
    }

    private fun updateUserOgrenilenKelimeNumber() {
        kullaniciRef?.document(Firebase.auth.currentUser!!.uid)?.update(
            "ogrenilenKelimeSayisi",
            FieldValue.increment(1)
        )
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