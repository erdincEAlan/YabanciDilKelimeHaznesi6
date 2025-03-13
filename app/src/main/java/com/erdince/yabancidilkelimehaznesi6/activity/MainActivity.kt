package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.input.key.Key
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.erdince.yabancidilkelimehaznesi6.*
import com.erdince.yabancidilkelimehaznesi6.util.isOnline
import com.erdince.yabancidilkelimehaznesi6.util.openNetworkSettings
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import com.erdince.yabancidilkelimehaznesi6.util.*
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbUserViewModel
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbWordViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var db: FirebaseFirestore? = null
    var auth = Firebase.auth
    private var user: FirebaseUser? = null
    lateinit var uid: String
    private var progressBar : LinearLayout?=null
    private var fragmentContainer : FragmentContainerView?=null
    private lateinit var navController: NavController
    private val dBWordViewModel: DbWordViewModel by viewModels()
    private val dbUserViewModel : DbUserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFirebase()
        progressBar = findViewById(R.id.progressBar)
        fragmentContainer = findViewById(R.id.mainFragmentContainer)
        setNavController()
        setBackPressed()
        stopProgressBar()
        setLocalDb()
    }

    private fun setLocalDb() {
        CoroutineScope(Dispatchers.IO).launch {
            dBWordViewModel.syncDatabases()
        }
    }

    private fun setNavController() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment).navController
        navController.addOnDestinationChangedListener() { naviController, destination, bundle ->
            handleQuizNavigation(bundle, naviController, destination)
            startProgressBar()
        }

    }


    private fun handleQuizNavigation(
        bundle: Bundle?,
        naviController: NavController,
        destination: NavDestination
    ) {
        bundle?.getBoolean(BundleSets.NavigationLoopBreaker.keyOfBundle)?.let { loopBreaker ->
            if (!loopBreaker) {
                if (bundle.getString(Keys.PreviousWordKey.key) != null) {
                    naviController.apply {
                        navigateWithCleaningLastBackStack(this, destination.id, bundle)
                    }
                }
                if (destination.label == "fragment_quiz_wrong_answer" && naviController.currentBackStack.value.last().destination.label == "fragment_quiz") {
                    naviController.apply {
                        navigateWithCleaningLastBackStack(this, destination.id, bundle)
                    }
                }
                if (naviController.currentBackStack.value.last().destination.label == "fragment_quiz_wrong_answer") {
                    naviController.apply {
                        navigateWithCleaningLastBackStack(this, destination.id, bundle)
                    }
                }
            }
        }
    }

    fun navigateWithCleaningLastBackStack(naviController: NavController, destinationId: Int, bundle: Bundle? = null) {
        naviController.apply {
            popBackStack(naviController.currentBackStack.value.let { it[it.lastIndex - 1] }.destination.id, false)
            bundle?.putAll(BundleSets.NavigationLoopBreaker.bundlePair)
            navigate(destinationId, bundle)
        }
    }

    override fun onStart() {
        super.onStart()
        setNetworkAlert()
        setFirebase()
        networkCheck()
    }

    private fun setBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    if (navController.currentBackStack.value.last().destination.label != "fragment_homepage") {
                        goBack()
                    }else {
                        finish()
                    }
            }
        })
    }
    fun backToHomepage(){
        startProgressBar()
        supportFragmentManager.popBackStack("", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, FragmentHomepage.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()

    }

    fun signOut(){
        auth.signOut()
        restartActivity()
    }
    fun goBack() {
        startProgressBar()
        navController.navigateUp()
    }


    fun changeFragment(fragment: Fragment, addToBackStack : Boolean = true, loadingBar : Boolean = true) {
        if (loadingBar){ startProgressBar()}
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFragmentContainer, fragment)
        if (addToBackStack){fragmentTransaction.addToBackStack(null)}
         fragmentTransaction.commitAllowingStateLoss()

    }
    fun changeFragmentWithoutLoadingBar(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()

    }
    fun restartFragment(fragment: Fragment){
        changeFragment(fragment, false)
    }

    fun returnUid() : String{
        return uid
    }
    fun startProgressBar(){
        fragmentContainer?.isVisible = false
        progressBar?.isVisible = true

    }
    fun stopProgressBar(){
        progressBar?.isVisible = false
        fragmentContainer?.isVisible = true
    }

    private fun setFirebase() {
        db = Firebase.firestore
        user = Firebase.auth.currentUser
        uid = user?.uid.toString()
    }

    fun throwDefaultWarning(){
        makeToast(getString(R.string.default_network_exception_msg))
    }


    private fun checkIsSignedInAndSwitchActivity() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navController.navigate(R.id.fragmentHomepage)
            if (isOnline(this)){
                dbUserViewModel.syncUserDatabases()
            }
        }else{
            if (isOnline(this)){
                navController.navigate(R.id.fragmentLogin)
            }else{
                showNetworkAlert()
            }
        }
    }

    private fun networkCheck() {
        checkIsSignedInAndSwitchActivity()
    }

    private fun showNetworkAlert() {
        setNetworkAlert().create().show()
    }

    private fun setNetworkAlert() : AlertDialog.Builder {
        var alert = AlertDialog.Builder(this)
        alert.setMessage(R.string.network_error_main)
        alert.setCancelable(false)
        alert.setPositiveButton(R.string.network_dialog_wifi_button) { _, _ ->
            openNetworkSettings("Wifi")
        }
        alert.setNegativeButton(R.string.network_dialog_mobile_data_button) { _, _ ->
            openNetworkSettings("Mobile")
        }
    return alert
    }

    override fun onDestroy() {
        super.onDestroy()
        progressBar = null
        fragmentContainer = null
        user = null
        db = null
    }

}