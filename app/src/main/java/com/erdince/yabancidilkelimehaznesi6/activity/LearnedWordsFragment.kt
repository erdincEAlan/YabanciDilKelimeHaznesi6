package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.adapter.WordListAdapter
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentLearnedWordsBinding
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentWordListBinding
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel


class LearnedWordsFragment : MainFragment() {
    private lateinit var fragmentLearnedWordsBinding : FragmentLearnedWordsBinding
    private val binding get() = fragmentLearnedWordsBinding

    private val wordViewModel : DbWordViewModel by viewModels()
    private var adapter : WordListAdapter?=null
    private var wordList = mutableListOf<WordModel>()
    private var filterList = mutableListOf<WordModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLearnedWordsBinding = FragmentLearnedWordsBinding.inflate(inflater,container,false)
        init()
        return binding.root
    }

    fun init() {
        initUI()
        observeViewModel()
    }

    private fun initUI() {
        prepareTheRecyclerView()
        setButtonClickers()
        initSearchView()
    }

    private fun prepareTheRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.wordListRcv.layoutManager = layoutManager
        setAdapter()
    }

    private fun initSearchView() {
        val filterProcessList = mutableListOf<WordModel>()
        with(binding){
            searchViewList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    android.util.Log.d("onQueryTextChange", "query: $query")
                    if (query?.isEmpty() == true) {
                        filterList = wordList

                    } else {
                        filterProcessList.clear()
                        for (row in wordList) {
                            if (row.wordIt?.lowercase()?.contains(query.toString().lowercase()) == true || row.wordMeaning?.lowercase()
                                    ?.contains(query.toString().lowercase()) == true)
                            {
                                if (!filterProcessList.contains(row)) {
                                    filterProcessList.add(row)
                                }
                            }

                        }
                        filterList = filterProcessList
                    }
                    adapter?.updateList(filterList)

                    return true
                }
            })
        }

    }



    private fun setButtonClickers() {
        binding.backButton.setOnClickListener {
            goBack()
        }
    }

    private fun observeViewModel(){
        wordViewModel.getWordList("customWord",true)
        wordViewModel.wordLiveData.observe(viewLifecycleOwner,::handleList)
    }

    private fun handleList(listResource : ResourceModel<Any?>) {
        if (listResource.success){
            wordList = listResource.data as MutableList<WordModel>
            adapter?.updateList(wordList)
            stopProgressBar()
        }else  {
            makeToast(getString(R.string.word_not_found_err))
            goBack()
        }

    }

    private fun setAdapter(wordsList: MutableList<WordModel> = wordList) {
        adapter = WordListAdapter(wordsList){
            changeFragment(FragmentWordEdit.newInstance(it.toString()),false)
        }
        binding.wordListRcv.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LearnedWordsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}