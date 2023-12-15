package com.erdince.yabancidilkelimehaznesi6.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.adapter.WordListAdapter
import com.erdince.yabancidilkelimehaznesi6.model.WordModel
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.util.restartActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class OgrenilenKelimelerListActivity : AppCompatActivity() {
    private var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private lateinit var uid: String
    private var kelimeDuzenleIntent: Intent? = null
    private var adapter: WordListAdapter? = null
    private var kelimeOgrenilenListeGeriButon: ImageButton? = null
    private var ogrenilenKelimelerRecycleView: RecyclerView? = null
    private var searchViewKelime: SearchView? = null
    private var kelimeListesi = mutableListOf<WordModel>()
    private var filtreListesi = mutableListOf<WordModel>()
    private var filtreListesi2 = mutableListOf<WordModel>()
    private var kelimeCekilen: WordModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ogrenilen_kelimeler_list)
        init()
    }
    override fun onStart() {
        super.onStart()
        checkListChangesAndUpdate()
    }

    private fun init() {
        setFirebase()
        initIntents()
        takeListAndSet()
        initUI()
    }

    private fun initUI() {
        setUI()
        setButtonClickers()
        initSearchView()
    }

    private fun setUI() {
        ogrenilenKelimelerRecycleView = findViewById(R.id.ogrenilenKelimelerRecyclerView)
        ogrenilenKelimelerRecycleView?.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        searchViewKelime = findViewById(R.id.ogrenilenKelimelerListSearchView)
        kelimeOgrenilenListeGeriButon = findViewById(R.id.ogrenilenKelimeBackButton)
        searchViewKelime = findViewById(R.id.ogrenilenKelimelerListSearchView)
    }

    private fun setButtonClickers() {
        kelimeOgrenilenListeGeriButon?.setOnClickListener {
        }
    }

    private fun initSearchView() {
        searchViewKelime?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("onQueryTextChange", "query: $query")
                if (query?.isEmpty() == true) {
                    filtreListesi = kelimeListesi

                } else {
                    filtreListesi2.clear()
                    for (row in kelimeListesi) {
                        if (row?.wordIt?.lowercase()
                                ?.contains(query.toString().lowercase()) == true
                            || row?.wordMeaning?.lowercase()
                                ?.contains(query.toString().lowercase()) == true
                        ) {
                            if (!filtreListesi2.contains(row)) {
                                filtreListesi2.add(row)
                            }
                        }

                    }
                    filtreListesi = filtreListesi2
                }
                setKelimeAdapter(filtreListesi)

                return true
            }
        })
    }

    private fun takeListAndSet() {

            db?.collection("kelimeler")?.whereEqualTo("kelimeSahipID", uid)
                ?.whereEqualTo("kelimeOgrenmeDurum", 1)
                ?.get()
                ?.addOnSuccessListener { documents ->
                    for (document in documents) {
                        kelimeCekilen = document.toObject()
                        kelimeCekilen?.wordId = document.id
                        kelimeListesi.add(kelimeCekilen!!)
                    }
                    setKelimeAdapter(kelimeListesi)
                }
                ?.addOnFailureListener {
                    makeToast("Veriler cekilirken sorun olustu, internet bağlantınızı kontrol edin")
                }

    }

    private fun setKelimeAdapter(mutableList: MutableList<WordModel>) {
        adapter = WordListAdapter(mutableList) {
            kelimeDuzenleIntent?.putExtra("kelimeID", it)
            startActivity(kelimeDuzenleIntent)
        }
        ogrenilenKelimelerRecycleView?.adapter = adapter


    }

    private fun initIntents() {

    }
    private fun checkListChangesAndUpdate() {
        if (kelimeListesi.isNotEmpty()) {
            restartActivity()
        }
    }
    private fun setFirebase() {
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user?.uid.toString()
    }

}