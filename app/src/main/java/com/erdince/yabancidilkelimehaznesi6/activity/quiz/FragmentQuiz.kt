package com.erdince.yabancidilkelimehaznesi6.activity.quiz

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.MainFragment
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizBinding
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.Keys
import com.erdince.yabancidilkelimehaznesi6.util.WordType
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val WORD_SRC_PARAM = "wordSource"
private const val PRE_WORD_PARAM = "lastWordId"


@AndroidEntryPoint
class FragmentQuiz : MainFragment() {

    private var questionWord: WordModel? = null
   private var lastWordId : String?=null
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
            lastWordId = it.getString(PRE_WORD_PARAM)
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
                findNavController().navigateUp()
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
                findNavController().navigateUp()
            }
            answerButton.setOnClickListener {
                if (answerReady){
                    takeTheAnswerAndInit()
                }
                answerButton.disableButton()
            }
        }

    }
    private fun checkBoxListeners(){
        with(binding){
            choiceLayout.choice1.choiceLl.setOnClickListener(){
                changeTapStates(1)
                choiceLayout.choice1.checkBox.isChecked = true
                choiceLayout.choice3.checkBox.isChecked  = false
                choiceLayout.choice2.checkBox.isChecked  = false
                updateCheckboxClickables()
            }
            choiceLayout.choice2.choiceLl.setOnClickListener(){
                changeTapStates(2)
                choiceLayout.choice2.checkBox.isChecked  = true
                choiceLayout.choice1.checkBox.isChecked  = false
                choiceLayout.choice3.checkBox.isChecked  = false
                updateCheckboxClickables()

            }
            choiceLayout.choice3.choiceLl.setOnClickListener(){
                changeTapStates(3)
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

    private fun changeTapStates(selectedChoice : Int?){
        with(binding){
            choiceLayout.choice1.animation.root.alpha = 1f
            choiceLayout.choice2.animation.root.alpha = 1f
            choiceLayout.choice3.animation.root.alpha = 1f
            choiceLayout.choice1.choiceText.setTextColor(resources.getColor(R.color.white))
            choiceLayout.choice2.choiceText.setTextColor(resources.getColor(R.color.white))
            choiceLayout.choice3.choiceText.setTextColor(resources.getColor(R.color.white))
            //selectedChoice?.alpha = 0.5f
            when(selectedChoice){
                1 ->{
                    choiceLayout.choice1.animation.root.alpha = 0.5f
                    choiceLayout.choice1.choiceText.setTextColor(resources.getColor(R.color.theme_orange_color))
                }
                2 -> {
                    choiceLayout.choice2.animation.root.alpha = 0.5f
                    choiceLayout.choice2.choiceText.setTextColor(resources.getColor(R.color.theme_orange_color))

                }
                3 -> {
                    choiceLayout.choice3.animation.root.alpha = 0.5f
                    choiceLayout.choice3.choiceText.setTextColor(resources.getColor(R.color.theme_orange_color))
                }

            }

        }

    }
    private fun setTheAnswer(newAnswerText : String){
        answerText = newAnswerText
        answerReady = true
        binding.answerButton.enableButton()
    }

    private fun FragmentQuizBinding.updateCheckboxClickables() {
        choiceLayout.choice1.choiceLl.isClickable = !choiceLayout.choice1.checkBox.isChecked
        choiceLayout.choice2.choiceLl.isClickable = !choiceLayout.choice2.checkBox.isChecked
        choiceLayout.choice3.choiceLl.isClickable = !choiceLayout.choice3.checkBox.isChecked
    }

    private fun takeTheAnswerAndInit() {
        if (answerText.isNotEmpty()){
            changeTapStates(null)
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
        configureAnimationsAndStart(false)
    }

    private fun switchToWrongAnswerPage() {
        findNavController().navigate(
            R.id.action_fragmentQuiz_to_fragmentQuizWrongAnswer, bundleOf(
                Pair(Keys.WordIdKey.key, questionWord?.wordId), Pair(Keys.WordTypeKey.key, wordSourceType)
            )
        )

    }


    private fun correctAnswer() {
       configureAnimationsAndStart(true)

    }

    private fun startAnimation(isAnswerTrue: Boolean) {
        with(binding){
            if (choiceLayout.choice1.checkBox.isChecked){
                if (!isAnswerTrue){
                    binding.choiceLayout.choice1.animation.animationView.setAnimation(R.raw.button_wrong_animation)
                }
                binding.choiceLayout.choice1.animation.animationView.playAnimation()


            }else if(choiceLayout.choice2.checkBox.isChecked){
                if (!isAnswerTrue){
                    binding.choiceLayout.choice2.animation.animationView.setAnimation(R.raw.button_wrong_animation)
                }
                binding.choiceLayout.choice2.animation.animationView.playAnimation()


            }else if (choiceLayout.choice3.checkBox.isChecked){
                if (!isAnswerTrue){
                    binding.choiceLayout.choice3.animation.animationView.setAnimation(R.raw.button_wrong_animation)
                }
                binding.choiceLayout.choice3.animation.animationView.playAnimation()


            } else{}
        }

    }

    private fun configureAnimationsAndStart(isAnswerTrue : Boolean) {
        val animationListener = object : AnimatorListener{
            override fun onAnimationStart(animation: Animator) {
                Log.d("Lottie","Animation Started")
            }

            override fun onAnimationEnd(animation: Animator) {
                if (isAnswerTrue) {
                    increaseKelimePointAndSwitch()
                }else{
                    switchToWrongAnswerPage()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.d("Lottie","Animation has been cancelled")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.d("Lottie","Animation repeat")
            }

        }
        binding.choiceLayout.choice1.animation.animationView.speed = 1.6f
        binding.choiceLayout.choice2.animation.animationView.speed = 1.6f
        binding.choiceLayout.choice3.animation.animationView.speed = 1.6f
        binding.choiceLayout.choice1.animation.animationView.addAnimatorListener(animationListener)
        binding.choiceLayout.choice2.animation.animationView.addAnimatorListener(animationListener)
        binding.choiceLayout.choice3.animation.animationView.addAnimatorListener(animationListener)
        startAnimation(isAnswerTrue)
    }

    private fun increaseKelimePointAndSwitch() {
        if (wordSourceType == WordType.CustomWord.value) {
            questionWord?.let { wordViewModel.increaseWordPoint(it) }
        }
        findNavController().apply {
            currentDestination?.id?.let {
                navigate(
                    it, bundleOf(
                        Pair(Keys.WordTypeKey.key, wordSourceType),
                        Pair(PRE_WORD_PARAM, questionWord?.wordId)
                    )
                )
            }
        }
        /*findNavController().apply { navigateWithCleaningLastBackStack(this,currentDestination!!.id,bundleOf(
            Pair(WordType.WordTypeKey.value, wordSourceType),
            Pair(PRE_WORD_PARAM, questionWord?.wordId)
        )) }*/
    }


    companion object {
        @JvmStatic
        fun newInstance(wordSource: String, previousWordId : String?=null) =
            FragmentQuiz().apply {
                arguments = Bundle().apply {
                    putString(WORD_SRC_PARAM, wordSource)
                    putString(PRE_WORD_PARAM, previousWordId)

                }
            }
    }
}
