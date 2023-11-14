package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizBinding
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

private const val WORD_SRC_PARAM = "wordSource"

private var db: FirebaseFirestore? = null
private var user: FirebaseUser? = null
private lateinit var uid: String
private var kelimelerRef : CollectionReference?=null
private var kullaniciRef : CollectionReference?=null
private var testSonucIntent: Intent? = null
private var soruKelime: KelimeModel? = null
private var soruListe = mutableListOf<KelimeModel?>()
private var cevapKelime: String? = null
@AndroidEntryPoint
class FragmentQuiz : Fragment() {
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
        setFirebase()
        setDatabaseReferences(wordSourceType!!)
        takeListAndSetQuestKelime()
    }

    private fun initUI() {
        setIntents()
        setButtonClickers()
    }


    private fun takeListAndSetQuestKelime() {
        wordViewModel.wordLiveData.observe(viewLifecycleOwner){
            soruKelime = it
            binding?.soruKelimeTextView?.text = soruKelime?.kelimeKendi?.capitalize(Locale.getDefault())
        }
        wordViewModel.observeRandomWord(wordSourceType!!).let {
            if (!it){
                requireActivity().makeToast("Sormak için kelime bulunmadığı veya hepsini öğrendiğiniz için anaekrana yönlendirildi. Kelime Ekle ekranından yeni kelime ekleyebilirsiniz")
                requireActivity().switchActivity("AnaEkranActivity")
            } else (activity as TestActivity).stopProgressBar()
        }

    }

    private fun setButtonClickers() {
        with(binding!!){
            backButton.setOnClickListener {
                requireActivity().switchActivity("AnaEkranActivity")
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
        (activity as TestActivity).changeFragment(fragmentQuizWrongAnswer)
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


    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.quizFragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
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

    private fun setIntents() {
        testSonucIntent = Intent(requireContext(), TestSonucActivity::class.java)
    }

    private fun setFirebase() {
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user?.uid.toString()
    }
    private fun setDatabaseReferences(wordType : String){
        kelimelerRef = db?.collection(wordType)
        kullaniciRef = db?.collection("user")
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