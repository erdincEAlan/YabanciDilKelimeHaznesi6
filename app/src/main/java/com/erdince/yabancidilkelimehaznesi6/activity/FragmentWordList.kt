package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.adapter.WordListAdapter
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentWordListBinding
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentWordList : MainFragment() {
    private val wordViewModel : DbWordViewModel by viewModels()
    private var adapter : WordListAdapter?=null
    private var wordList = mutableListOf<WordModel>()
    private var filterList = mutableListOf<WordModel>()
    private lateinit var fragmentBinding : FragmentWordListBinding
    private val binding get() = fragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentWordListBinding.inflate(inflater,container,false)
        init()
        return binding.root
    }

    fun init() {
        initUI()
        observeViewModel()
    }

    private fun initUI() {
        setAdapter()
        setButtonClickers()
        setCheckBoxCheckListeners()
        initSearchView()
    }

    private fun initSearchView() { 
        var filterProcessList = mutableListOf<WordModel>()
        with(binding){
            searchViewKelime.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("onQueryTextChange", "query: $query")
                if (query?.isEmpty() == true) {
                    filterList = wordList

                } else {
                    filterProcessList.clear()
                    for (row in wordList) {
                        if (row.kelimeKendi?.lowercase()?.contains(query.toString().lowercase()) == true || row.kelimeAnlam?.lowercase()
                            ?.contains(query.toString().lowercase()) == true)
                        {
                            if (!filterProcessList.contains(row)) {
                                filterProcessList.add(row)
                            }
                        }

                    }
                    filterList = filterProcessList
                }
                setAdapter(filterList)

            return true
        }
    })
}

    }

    private fun setCheckBoxCheckListeners() {
        with(binding){
            wordItCheckBox.setOnCheckedChangeListener { _, _ ->
                setCheckboxFilters()

            }
            wordMeaningCheckBox.setOnCheckedChangeListener { _, _ ->
                setCheckboxFilters()

            }
            wordExampleCheckBox.setOnCheckedChangeListener { _, _ ->
                setCheckboxFilters()

            }
        }

    }

    private fun setButtonClickers() {
        binding.kelimeAraBackButton.setOnClickListener {
            goBack()
        }
    }

    private fun observeViewModel(){
        wordViewModel.getWordList("customWords")
        wordViewModel.wordLiveData.observe(viewLifecycleOwner,::handleList)
    }

    private fun handleList(listResource : ResourceModel) {
        if (listResource.success){
            wordList = listResource.data as MutableList<WordModel>
            setAdapter()
        }else  {makeToast(getString(R.string.word_not_found_err))}
            stopProgressBar()
    }

    private fun setAdapter(wordsList: MutableList<WordModel> = wordList) {
        adapter = WordListAdapter(wordsList){}
        binding.kelimeListeRecyclerView.adapter = adapter
    }



    private fun setCheckboxFilters() {
        with(binding){
           val wordStatus = wordItCheckBox.isChecked
            val wordStatus01 = wordMeaningCheckBox.isChecked
            val wordStatus02 = wordExampleCheckBox.isChecked
            adapter?.updateList(wordList, wordStatus, wordStatus01, wordStatus02)
        }


    }



    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentWordList().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
