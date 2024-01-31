package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizBinding
import com.erdince.yabancidilkelimehaznesi6.model.QuestionWordModel
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val WORD_SRC_PARAM = "wordSource"


@AndroidEntryPoint
class FragmentQuiz : MainFragment() {

    private var questionWord: WordModel? = null
    private var questionWordsList: MutableList<QuestionWordModel>? = null
    private var choiceWords: MutableList<String> = mutableListOf()
    private var answerText: String = ""
    private val wordViewModel: DbWordViewModel by viewModels()
    private lateinit var __binding: FragmentQuizBinding
    private val binding get() = __binding
    private var wordSourceType: String? = null
    private var answerReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordSourceType = it.getString(WORD_SRC_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding = FragmentQuizBinding.inflate(inflater, container, false)
        init()
        return binding.root
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
        checkBoxListeners()
    }


    private fun takeListAndSetQuestKelime() {
        wordViewModel.observeRandomWord(wordSourceType!!)
        wordViewModel.wordLiveData.observe(viewLifecycleOwner) {resource ->
            if (resource.success) {
                if (resource.data != null) {
                    questionWord = resource.data as WordModel
                    questionWord?.wordMeaning?.let { it1 -> choiceWords.add(it1) }
                    while (choiceWords.size < 3){
                        resources.getStringArray(R.array.randomChoices).random().let {choiceWord ->
                            if (!choiceWords.contains(choiceWord)){
                                choiceWords.add(choiceWord)
                            }
                        }
                    }
                    setTextViews()
                }
                stopProgressBar()
            } else {
                requireActivity().makeToast("Sormak için kelime bulunmadığı veya hepsini öğrendiğiniz için anaekrana yönlendirildi. Kelime Ekle ekranından yeni kelime ekleyebilirsiniz")
                goBack()
            }

        }


    }

    private fun setTextViews() {
        choiceWords.shuffle()
        if (questionWord?.wordPoint != null && questionWord?.wordPoint != 0){
            val wordPointAsString = questionWord?.wordPoint.toString()+getString(R.string.quiz_word_point_status_text)
            binding.questionWordPointStatus.text = wordPointAsString
            binding.questionWordPointStatus.isVisible = true
        }
        binding.choiceLayout.choice1.choiceText.text = choiceWords[0].capitalize(Locale.getDefault())
        binding.choiceLayout.choice2.choiceText.text = choiceWords[1].capitalize(Locale.getDefault())
        binding.choiceLayout.choice3.choiceText.text = choiceWords[2].capitalize(Locale.getDefault())
        binding.questionTextView.text = questionWord?.wordIt?.capitalize(Locale.getDefault())

    }

    private fun setButtons() {
        with(binding) {
            backButton.setOnClickListener {
                backToHomepage()
            }
            answerButton.setOnClickListener {
                if (answerReady){
                    takeTheAnswerAndInit()
                }else it.alpha = 0.5f

            }
        }

    }
    private fun checkBoxListeners(){
        with(binding){
            choiceLayout.choice1.choiceLl.setOnClickListener(){
                choiceLayout.choice1.checkBox.isChecked = true
                choiceLayout.choice3.checkBox.isChecked  = false
                choiceLayout.choice2.checkBox.isChecked  = false
                updateCheckboxClickables()
            }
            choiceLayout.choice2.choiceLl.setOnClickListener(){
                choiceLayout.choice2.checkBox.isChecked  = true
                choiceLayout.choice1.checkBox.isChecked  = false
                choiceLayout.choice3.checkBox.isChecked  = false
                updateCheckboxClickables()

            }
            choiceLayout.choice3.choiceLl.setOnClickListener(){
                choiceLayout.choice3.checkBox.isChecked  = true
                choiceLayout.choice1.checkBox.isChecked  = false
                choiceLayout.choice2.checkBox.isChecked   = false
                updateCheckboxClickables()
                setTheAnswer(choiceWords[2])
            }
            choiceLayout.choice1.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    setTheAnswer(choiceWords[0])
                }
            }
            choiceLayout.choice2.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    setTheAnswer(choiceWords[1])
                }
            }
            choiceLayout.choice3.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    setTheAnswer(choiceWords[2])
                }
            }

        }

    }
    private fun setTheAnswer(newAnswerText : String){
        answerText = newAnswerText
        answerReady = true
        binding.answerButton.alpha = 1f
    }

    private fun FragmentQuizBinding.updateCheckboxClickables() {
        choiceLayout.choice1.choiceLl.isClickable = !choiceLayout.choice1.checkBox.isChecked
        choiceLayout.choice2.choiceLl.isClickable = !choiceLayout.choice2.checkBox.isChecked
        choiceLayout.choice3.choiceLl.isClickable = !choiceLayout.choice3.checkBox.isChecked
    }

    private fun takeTheAnswerAndInit() {
        if (answerText.isNotEmpty()){
            if (takeStringsAndMakeReadyToQuestioning(questionWord?.wordMeaning!!) == takeStringsAndMakeReadyToQuestioning(answerText)
            ) {
                correctAnswer()
            } else {
                incorrectAnswer()
            }
        }

    }

    private fun takeStringsAndMakeReadyToQuestioning(text: String): String {
        return text.lowercase().replace("\\p{Punct}|\\s".toRegex(), "")
    }

    private fun incorrectAnswer() {
        val fragmentQuizWrongAnswer = FragmentQuizWrongAnswer.newInstance(questionWord?.wordId!!, wordSourceType!!)
        changeFragment(fragmentQuizWrongAnswer)
    }


    private fun correctAnswer() {
        requireActivity().makeToast("Cevap Doğru")
        increaseKelimePointAndSwitch()
    }

    private fun increaseKelimePointAndSwitch() {
        if (wordSourceType == "customWord") {
            questionWord?.let { wordViewModel.increaseWordPoint(it) }
        }
        changeFragment(FragmentQuiz.newInstance(wordSourceType.toString()), false)
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