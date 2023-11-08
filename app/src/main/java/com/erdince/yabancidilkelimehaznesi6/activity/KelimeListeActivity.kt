package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.adapter.KelimeAdapter
import com.erdince.yabancidilkelimehaznesi6.model.KelimeModel
import com.erdince.yabancidilkelimehaznesi6.util.restartActivity
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class KelimeListeActivity : AppCompatActivity() {

    private var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private lateinit var uid: String
    private var kelimeDuzenleIntent: Intent? = null
    private var searchViewKelime: SearchView? = null
    private var adapter: KelimeAdapter? = null
    private var kelimeListesi = mutableListOf<KelimeModel?>()
    private var filtreListesi = mutableListOf<KelimeModel?>()
    private var filtreListesi2 = mutableListOf<KelimeModel?>()
    private var kelimeCekilen: KelimeModel? = null
    private var kelimeListRecyclerView: RecyclerView? = null
    private lateinit var kelimeCheckBox: CheckBox
    private lateinit var kelimeAnlamCheckBox: CheckBox
    private lateinit var kelimeOrnekCheckBox: CheckBox
    private var kelimeListeGeriButon: ImageButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelime_liste)
        init()
    }

    override fun onStart() {
        super.onStart()
        checkListChangesAndUpdate()
    }

    private fun checkListChangesAndUpdate() {
        if (kelimeListesi.isNotEmpty()) {
            restartActivity()
        }
    }

    fun init() {
        setFirebase()
        initIntents()
        takeListAndSet()
        initUI()
    }

    private fun initUI() {
        setUI()
        setButtonClickers()
        setCheckBoxCheckListeners()
        initSearchView()
    }

    private fun initSearchView() {


        searchViewKelime?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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
                setKelimeAdapter(filtreListesi)

                return true
            }
        })
    }

    private fun setCheckBoxCheckListeners() {
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

    private fun setButtonClickers() {
        kelimeListeGeriButon?.setOnClickListener {
            switchActivity("AnaEkranActivity")
        }
    }

    private fun setUI() {
        searchViewKelime = findViewById(R.id.searchViewKelime)
        kelimeListRecyclerView = findViewById(R.id.kelimeListeRecyclerView)
        kelimeListRecyclerView!!.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        kelimeCheckBox = findViewById(R.id.kelimeCheckBox)
        kelimeAnlamCheckBox = findViewById(R.id.kelimeAnlamCheckBox)
        kelimeOrnekCheckBox = findViewById(R.id.kelimeOrnekCheckBox)
        kelimeListeGeriButon = findViewById(R.id.kelimeAraBackButton)
    }


    private fun takeListAndSet() {

        db?.collection("kelimeler")?.whereEqualTo("kelimeSahipID", uid)
            ?.get()
            ?.addOnSuccessListener { documents ->
                for (document in documents) {
                    kelimeCekilen = document.toObject()
                    kelimeCekilen?.kelimeID = document.id
                    kelimeListesi.add(kelimeCekilen)
                }
                setKelimeAdapter(kelimeListesi)
            }
            ?.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Veriler cekilirken sorun olustu", exception)
            }


    }

    fun setKelimeAdapter(mutableList: MutableList<KelimeModel?>) {
        adapter = KelimeAdapter(mutableList) {
            kelimeDuzenleIntent?.putExtra("kelimeID", it)
            startActivity(kelimeDuzenleIntent)
        }
        kelimeListRecyclerView?.adapter = adapter

    }

    private fun checkBoxFiltre() {

        val kelimeDurumm1 = kelimeCheckBox.isChecked
        val kelimeAnlamDurumm1 = kelimeAnlamCheckBox.isChecked
        val kelimeOrnekDurumm1 = kelimeOrnekCheckBox.isChecked
        adapter?.updateList(kelimeListesi, kelimeDurumm1, kelimeAnlamDurumm1, kelimeOrnekDurumm1)

    }


    private fun initIntents() {
        kelimeDuzenleIntent =
            Intent(this, KelimeDuzenleActivity::class.java)
    }

    private fun setFirebase() {
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user?.uid.toString()
    }

}