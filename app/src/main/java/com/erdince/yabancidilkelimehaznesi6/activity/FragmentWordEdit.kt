package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentWordEditBinding
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
                goBack()
            }
            backButton.setOnClickListener(){
                goBack()
            }
            deleteButton.setOnClickListener(){
                dBWordViewModel.deleteWord(wordId!!)
                goBack()
            }
        }
    }
    private fun updateTheWord(){
        with(binding){
            theWord?.kelimeKendi = wordItEditText.text.toString()
            theWord?.kelimeAnlam = wordMeaningEditText.text.toString()
            theWord?.kelimeOrnekCumle = wordExampleEditText.text.toString()
            dBWordViewModel.updateWord(theWord!!,wordId!!)
        }


    }
    private fun setCurrentWordToEditTexts(){
        with(binding){
            dBWordViewModel.wordLiveData.observe(viewLifecycleOwner){
                if (it.success){
                    theWord = it.data as WordModel
                    wordItEditText.setText(theWord?.kelimeKendi)
                    wordMeaningEditText.setText(theWord?.kelimeAnlam)
                    wordExampleEditText.setText(theWord?.kelimeOrnekCumle)
                    stopProgressBar()
                }else{
                    goBack()
                    makeToast("Bir sorun oluştu, internetinizi kontrol edin")
                }
            }
            dBWordViewModel.getWordFromId(wordId!!,"customWord")
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