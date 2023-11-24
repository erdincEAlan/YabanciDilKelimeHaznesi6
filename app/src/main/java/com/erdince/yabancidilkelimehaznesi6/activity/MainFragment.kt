package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    arguments.let {

    }
    }
    fun changeFragment(fragment : Fragment){
        (activity as MainActivity).changeFragment(fragment)
    }
    fun stopProgressBar(){
        (activity as MainActivity).stopProgressBar()
    }
    fun returnUid() : String{
        return  (activity as MainActivity).returnUid()
    }

}