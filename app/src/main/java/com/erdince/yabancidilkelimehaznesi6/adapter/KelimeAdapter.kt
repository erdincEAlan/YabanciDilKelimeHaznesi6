package com.erdince.yabancidilkelimehaznesi6.adapter

/*

class KelimeAdapter(kelimeListesi : MutableList<KelimeModel?>, var onClik :(String?) -> Unit) : RecyclerView.Adapter<KelimeAdapter.ViewHolder>() {

    var kelimeListe: MutableList<KelimeModel?>? =null
    var isKelime = true
    var isAnlam = true
    var isOrnek = true

    init {
    kelimeListe = kelimeListesi
    }

    fun updateList(listeKelime : MutableList<KelimeModel?>, isKelime1 : Boolean = true, isAnlam1 : Boolean = true, isOrnek1 : Boolean = true){
        kelimeListe = listeKelime
        isKelime = isKelime1
        isAnlam = isAnlam1
        isOrnek = isOrnek1
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: RowItemKelimeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = RowItemKelimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {

        kelimeListe?.let {
            return it.size
        }
        return 0

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        with(holder){


            binding.apply {
                var kelimeItem = kelimeListe?.get(position)
                if (isKelime){
                    txtKelime.text=kelimeItem?.kelimeKendi
                    txtKelime.visibility=View.VISIBLE
                }else{
                    txtKelime.visibility=View.GONE

                }
                if (isAnlam){
                    txtAnlam.text=kelimeItem?.kelimeAnlam
                    txtAnlam.visibility=View.VISIBLE
                }else{
                    txtAnlam.visibility=View.GONE

                }
                if (isOrnek){
                    txtOrnekCumle.text=kelimeItem?.kelimeOrnekCumle
                    txtOrnekCumle.visibility=View.VISIBLE
                }else{
                    txtOrnekCumle.visibility=View.GONE

                }
                kelimeDuzenleButton.setOnClickListener {

                    onClik(kelimeItem?.kelimeID)

                }




            }

        }

    }






}*/
