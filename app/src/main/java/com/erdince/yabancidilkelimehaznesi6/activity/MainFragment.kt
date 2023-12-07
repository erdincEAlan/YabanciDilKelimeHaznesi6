package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    arguments.let {

    }
    }
    fun changeFragment(fragment : Fragment, addTobackStack : Boolean = true){
        (activity as MainActivity).changeFragment(fragment, addTobackStack)
    }
    fun stopProgressBar(){
        (activity as MainActivity).stopProgressBar()
    }
    fun returnUid() : String{
        return  (activity as MainActivity).returnUid()
    }
    fun makeToast(msg : String){
        (activity as MainActivity).makeToast(msg)
    }
    fun restartFragment(currentFragment : Fragment){
        (activity as MainActivity).restartFragment(currentFragment)
    }
    fun goBack(){
        (activity as MainActivity).goBack()
    }
    fun changeFragmentWithoutLoadingBar(fragment : Fragment){
        (activity as MainActivity).changeFragmentWithoutLoadingBar(fragment)
    }
    fun backToHomepage(){
        (activity as MainActivity).backToHomepage()
    }

}