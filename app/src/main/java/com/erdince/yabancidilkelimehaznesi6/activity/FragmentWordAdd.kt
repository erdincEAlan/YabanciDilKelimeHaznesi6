package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentWordAddBinding
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentWordAdd : MainFragment() {

    private val wordViewModel : DbWordViewModel by viewModels()
    private lateinit var fragmentBinding : FragmentWordAddBinding
    private val binding get() = fragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentWordAddBinding.inflate(inflater,container,false)
        init()
        stopProgressBar()
        return binding.root
    }

    private fun init() {
        initUI()
    }

    private fun initUI() {
        setButtonClickers()
    }



    private fun setButtonClickers() {
        with(binding){
            kelimeKaydetButton.setOnClickListener {
                saveKelime()
            }
            kelimeEkleBackButton.setOnClickListener {
                goBack()
            }
        }
    }

    private fun saveKelime() {
        if (checkIfFieldsEmpty()){
            saveTheWord()
            restartFragment(newInstance())
        }
    }




    private fun checkIfFieldsEmpty() : Boolean {
        with(binding){
            return if (kelimeEkleKelimeGiris.text.isEmpty() || kelimeEkleAnlamGiris.text.isEmpty()){
                makeToast("Lütfen zorunlu olan kelime ve anlam alanlarını doldurduğunuzdan emin olun")
                false
            }else{
                true
            }

        }


    }

    private fun saveTheWord() {
        wordViewModel.addCustomWord(
            WordModel(
            wordIt = binding.kelimeEkleKelimeGiris.text.toString(),
            wordMeaning = binding.kelimeEkleAnlamGiris.text.toString(),
            wordExample = binding.kelimeEkleOrnekCumleGiris.text.toString()
            )
        )
    }







    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentWordAdd().apply {
                arguments = Bundle().apply {
                }
            }
    }
}