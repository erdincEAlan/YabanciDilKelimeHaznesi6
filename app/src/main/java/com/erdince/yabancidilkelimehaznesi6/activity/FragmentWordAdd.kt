package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentWordAddBinding
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentWordAdd : MainFragment() {

    private val wordViewModel : DbWordViewModel by viewModels()
    private var binding: FragmentWordAddBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordAddBinding.inflate(inflater, container, false)
        init()
        stopProgressBar()
        return binding?.root
    }

    private fun init() {
        initUI()
    }

    private fun initUI() {
        setButtonClickers()
    }



    private fun setButtonClickers() {
        binding?.apply {
            kelimeKaydetButton.setOnClickListener {
                saveKelime()
            }
            kelimeEkleBackButton.setOnClickListener {
                findNavController().navigateUp()
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
        binding?.apply {
            if (kelimeEkleKelimeGiris.text.isEmpty() || kelimeEkleAnlamGiris.text.isEmpty()) {
                makeToast("Lütfen zorunlu olan kelime ve anlam alanlarını doldurduğunuzdan emin olun")
                return false
            }
        }
        return true
    }

    private fun saveTheWord() {
        binding?.apply {
            wordViewModel.addCustomWord(
                WordModel(
                    wordIt = kelimeEkleKelimeGiris.text.toString(),
                    wordMeaning = kelimeEkleAnlamGiris.text.toString(),
                    wordExample = kelimeEkleOrnekCumleGiris.text.toString()
                )
            )
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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