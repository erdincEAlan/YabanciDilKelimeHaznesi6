package com.erdince.yabancidilkelimehaznesi6.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.erdince.yabancidilkelimehaznesi6.databinding.RowItemKelimeBinding
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import dagger.hilt.android.AndroidEntryPoint


class WordListAdapter(
    private var wordList: MutableList<KelimeModel>,
    private val onClick: (String?) -> Unit
) : RecyclerView.Adapter<WordListAdapter.WordListViewHolder>() {
    var wordItStatus = true
    var meaningStatus = true
    var exampleStatus = true

     class WordListViewHolder(val binding: RowItemKelimeBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val binding =
            RowItemKelimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) {
        val wordItem = wordList[position]

        with(holder.binding) {
            if (wordItStatus) {
                txtWord.text = wordItem.kelimeKendi
                txtWord.visibility = View.VISIBLE
            } else {
                txtWord.visibility = View.GONE
            }

            if (meaningStatus) {
                txtMeaning.text = wordItem.kelimeAnlam
                txtMeaning.visibility = View.VISIBLE
            } else {
                txtMeaning.visibility = View.GONE
            }

            if (exampleStatus) {
                txtExample.text = wordItem.kelimeOrnekCumle
                txtExample.visibility = View.VISIBLE
            } else {
                txtExample.visibility = View.GONE
            }

            wordEditButton.setOnClickListener {
                onClick(wordItem.kelimeID)
            }
        }
    }

    fun updateList(
        listeKelime: MutableList<KelimeModel>,
        wordStatus: Boolean = true,
        meaningStatus1: Boolean = true,
        exampleStatus1: Boolean = true
    ) {
        wordList = listeKelime
        wordItStatus = wordStatus
        meaningStatus = meaningStatus1
        exampleStatus = exampleStatus1
        notifyDataSetChanged()
    }
}

