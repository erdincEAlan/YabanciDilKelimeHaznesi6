package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.room.Room
import com.erdince.yabancidilkelimehaznesi6.util.interfaces.WordDao
import com.erdince.yabancidilkelimehaznesi6.util.makeToast
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.LocalWordDb
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainFragment : Fragment() {



    fun changeFragment(fragment : Fragment, addTobackStack : Boolean = true){
        (activity as MainActivity).changeFragment(fragment, addTobackStack)
    }

    fun navigateWithCleaningLastBackStack(naviController: NavController, destinationId: Int, bundle: Bundle? = null) {
        (activity as MainActivity).navigateWithCleaningLastBackStack(naviController, destinationId, bundle)
    }
    fun throwDefaultWarning(){
        (activity as MainActivity).throwDefaultWarning()
    }
    fun stopProgressBar(){
        (activity as MainActivity).stopProgressBar()
    }
    fun startProgressBar(){
        (activity as MainActivity).startProgressBar()
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
    fun signOut(){
        (activity as MainActivity).signOut()
    }

}