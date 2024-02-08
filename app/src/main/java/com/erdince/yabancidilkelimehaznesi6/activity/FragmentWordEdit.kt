package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentWordEditBinding
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import dagger.hilt.android.AndroidEntryPoint


private const val IdWord_PARAM = "wordId"
@AndroidEntryPoint
class FragmentWordEdit : MainFragment() {
    private lateinit var fragmentWordEditBinding : FragmentWordEditBinding
    val binding get() = fragmentWordEditBinding
    val dBWordViewModel : DbWordViewModel by viewModels()
    private var wordId: String? = null
    private var theWord : WordModel?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordId = it.getString(IdWord_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentWordEditBinding = FragmentWordEditBinding.inflate(inflater,container,false)
        init()
        return binding.root
    }

    private fun init() {
        setButtons()
        setCurrentWordToEditTexts()
    }

    private fun setButtons(){
        with(binding){
            saveButton.setOnClickListener(){
                updateTheWord()
                changeFragment(FragmentWordList.newInstance(),false)
            }
            backButton.setOnClickListener(){
                changeFragment(FragmentWordList.newInstance(),false)
            }
            deleteButton.setOnClickListener(){
                dBWordViewModel.deleteWord(wordId!!)
                changeFragment(FragmentWordList.newInstance(),false)
            }
            resetWordStatusBt.setOnClickListener() {
                dBWordViewModel.resetTheWordStatus(theWord)
                it.isClickable = false
                it.alpha = 0.5f
                restartFragment(FragmentWordEdit.newInstance(wordId.toString()))
            }
        }
    }
    private fun updateTheWord(){
        with(binding){
            theWord?.wordIt = wordItEditText.text.toString()
            theWord?.wordMeaning = wordMeaningEditText.text.toString()
            theWord?.wordExample = wordExampleEditText.text.toString()
            dBWordViewModel.updateWord(theWord)
        }


    }
    private fun setCurrentWordToEditTexts(){
        with(binding){
            dBWordViewModel.wordLiveData.observe(viewLifecycleOwner){
                if (it.success){
                    handleUI(it)
                    stopProgressBar()
                }else{
                    goBack()
                    makeToast("Bir sorun oluştu, internetinizi kontrol edin")
                }
            }
            dBWordViewModel.getWordFromId(wordId!!,"customWord")
        }
    }

    private fun handleUI(it: ResourceModel<Any?>) {
        fillUpEditTexts(it)
        binding.wordPointStatusInfo.text = theWord?.wordPoint.toString() + getString(R.string.of10)
        if (theWord?.wordLearningStatus == true)
            binding.wordLearnStatusInfo.text = getString(R.string.learnedWordStatusText)
        else binding.wordLearnStatusInfo.text = getString(R.string.unLearnedWordStatusText)
    }

    private fun fillUpEditTexts(it: ResourceModel<Any?>) {
        with(binding) {
            theWord = it.data as WordModel
            wordItEditText.setText(theWord?.wordIt)
            wordMeaningEditText.setText(theWord?.wordMeaning)
            wordExampleEditText.setText(theWord?.wordExample)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(wordId: String) =
            FragmentWordEdit().apply {
                arguments = Bundle().apply {
                    putString(IdWord_PARAM, wordId)
                }
            }
    }
}