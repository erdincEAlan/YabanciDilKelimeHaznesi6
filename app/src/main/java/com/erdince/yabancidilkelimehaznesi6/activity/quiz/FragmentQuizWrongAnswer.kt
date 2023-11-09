package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizWrongAnswerBinding
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private const val PARAM_WORD_ID : String = "wordId"
private const val PARAM_QUIZ_TYPE : String = "quizType"


class FragmentQuizWrongAnswer : Fragment() {
    var publicWord : KelimeModel? =null
    private var preparedWordsRef: CollectionReference? = null
    var db : FirebaseFirestore?=null
    private var kelimeKendi : String? = null
    private var kelimeAnlam : String? = null
    private var kelimeOrnek : String? = null
    private var  kelimelerRef : CollectionReference? = null
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
        prepare()
        publicWordsToCustomWords(wordId.toString())
        initUI()
    }

    private fun prepare() {
        setFirebase()
        setDocumentReferences()

    }
    private fun publicWordsToCustomWords(wordId : String) {
        preparedWordsRef?.document(wordId)?.get()?.addOnSuccessListener {

           var publicToCustomWord = it.toObject(KelimeModel::class.java)
            publicToCustomWord?.kelimeOgrenmeDurum = 0
            publicToCustomWord?.kelimePuan = 1
            publicToCustomWord?.kelimeSahipID = (activity as TestActivity).returnUid()
            publicToCustomWord?.wordType = "preparedWord"
            publicWord = publicToCustomWord
            initTextViews()
            if (publicToCustomWord != null) {
                db?.collection("kelimeler")?.add(publicToCustomWord)
            }

        }
    }

    private fun initUI() {
        setButtonClickers()
    }

    private fun setButtonClickers() {
        with(binding){
            bitirButton.setOnClickListener {
                requireActivity().switchActivity("AnaEkranActivity")
            }
            sonrakiKelimeButton.setOnClickListener {
                val quizFragment : FragmentQuiz = FragmentQuiz.newInstance(quizType.toString())
                changeFragment(quizFragment)
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



    private fun setDocumentReferences() {
        kelimelerRef = db?.collection("kelimeler")
         preparedWordsRef = db?.collection("preparedWords")
    }
    private fun setFirebase() {
        db = Firebase.firestore
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