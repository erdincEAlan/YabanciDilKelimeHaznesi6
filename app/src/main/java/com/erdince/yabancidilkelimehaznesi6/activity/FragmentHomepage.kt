package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.quiz.FragmentQuizSourceSelection
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentHomepageBinding
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentQuizBinding
import com.erdince.yabancidilkelimehaznesi6.util.createAndShowDialog
import com.erdince.yabancidilkelimehaznesi6.util.switchActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase




class FragmentHomepage : MainFragment() {

    private lateinit var __binding : FragmentHomepageBinding
    private val binding get() = __binding
    private var db: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private lateinit var uid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        FirebaseApp.initializeApp(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        __binding= FragmentHomepageBinding.inflate(inflater,container,false)
        init()
        return binding.root
    }

    private fun init() {
        setFirebase()
        userDatabaseDocumentControl()
        setButtons()
    }

    private fun setButtons() {
        with(binding){
            profilButon.setOnClickListener {

            }
            testButon.setOnClickListener {
                changeFragment(FragmentQuizSourceSelection.newInstance())
            }
            kelimeEkleButon.setOnClickListener {

            }
            listeGoruntuleButon.setOnClickListener {

            }
        }

    }



    private fun userDatabaseDocumentControl() {
        db?.collection("user")?.document(uid)?.get()?.addOnSuccessListener { document ->
            if (!document.exists()) {
                val kullanici = mutableMapOf(
                    "uid" to uid,
                    "kullaniciAdi" to user?.displayName,
                    "kelimeSayisi" to 0,
                    "ogrenilenKelimeSayisi" to 0,
                    "dogrulamaTuru" to "google"
                )
                db?.collection("user")?.document(uid)?.set(kullanici)
            }
        }
    }


    private fun setFirebase() {

        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user!!.uid

    }


    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentHomepage().apply {
                arguments = Bundle().apply {

                }
            }
    }
}