package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.erdince.yabancidilkelimehaznesi6.adapter.KelimeAdapter
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentWordListBinding
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.model.ResourceModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentWordList : MainFragment() {
    private val wordViewModel : DbWordViewModel by viewModels()
    private var adapter: KelimeAdapter? = null
    private var wordList = mutableListOf<KelimeModel?>()
    private var filtreListesi = mutableListOf<KelimeModel?>()
    private var filtreListesi2 = mutableListOf<KelimeModel?>()
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
        observeViewModel()
        initUI()
    }

    private fun initUI() {

        setButtonClickers()
        setCheckBoxCheckListeners()
        initSearchView()
    }

    private fun initSearchView() {

with(binding){
    searchViewKelime.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(p0: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(query: String?): Boolean {
            Log.d("onQueryTextChange", "query: $query")
            if (query?.isEmpty() == true) {
                filtreListesi = wordList

            } else {
                filtreListesi2.clear()
                for (row in wordList) {
                    if (row?.kelimeKendi?.lowercase()
                            ?.contains(query.toString().lowercase()) == true
                        || row?.kelimeAnlam?.lowercase()
                            ?.contains(query.toString().lowercase()) == true
                    ) {
                        if (!filtreListesi2.contains(row)) {
                            filtreListesi2.add(row)
                        }
                    }

                }
                filtreListesi = filtreListesi2
            }
            updateAdapter(filtreListesi)

            return true
        }
    })
}

    }

    private fun setCheckBoxCheckListeners() {
        with(binding){
            kelimeCheckBox.setOnCheckedChangeListener { _, _ ->
                checkBoxFiltre()

            }
            kelimeAnlamCheckBox.setOnCheckedChangeListener { _, _ ->
                checkBoxFiltre()

            }
            kelimeOrnekCheckBox.setOnCheckedChangeListener { _, _ ->
                checkBoxFiltre()

            }
        }

    }

    private fun setButtonClickers() {
        binding.kelimeAraBackButton.setOnClickListener {

        }
    }

private fun observeViewModel(){
    wordViewModel.getWordList("customWords")
    wordViewModel.wordLiveData.observe(viewLifecycleOwner,::handleList)
}

    private fun handleList(listResource : ResourceModel) {
        if (listResource.success){
            wordList = listResource.data as MutableList<KelimeModel?>
            updateAdapter()
            stopProgressBar()
        }else {
            makeToast("Kelime bulunamadı")
            stopProgressBar()
        }

    }

    private fun updateAdapter(wordsList: MutableList<KelimeModel?> = wordList) {
        adapter = KelimeAdapter(wordsList) {
            //kelimeDuzenleIntent?.putExtra("kelimeID", it)
            //startActivity(kelimeDuzenleIntent)
        }
        binding.kelimeListeRecyclerView.adapter = adapter
    }



    private fun checkBoxFiltre() {
    with(binding){
        val kelimeDurumm1 = kelimeCheckBox.isChecked
        val kelimeAnlamDurumm1 = kelimeAnlamCheckBox.isChecked
        val kelimeOrnekDurumm1 = kelimeOrnekCheckBox.isChecked
        adapter?.updateList(wordList, kelimeDurumm1, kelimeAnlamDurumm1, kelimeOrnekDurumm1)
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
