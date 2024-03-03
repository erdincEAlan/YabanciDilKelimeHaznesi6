package com.erdince.yabancidilkelimehaznesi6.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.input.key.Key
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
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
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var db: FirebaseFirestore? = null
    var auth = Firebase.auth
    private var user: FirebaseUser? = null
    lateinit var uid: String
    private var progressBar : LinearLayout?=null
    private var fragmentContainer : FragmentContainerView?=null
    val fragmentManager = supportFragmentManager
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFirebase()
        progressBar = findViewById(R.id.progressBar)
        fragmentContainer = findViewById(R.id.mainFragmentContainer)
        setNavController()
        setBackPressed()
        stopProgressBar()
    }

    private fun setNavController() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment).navController
        navController.addOnDestinationChangedListener() { naviController, destination, bundle ->
            naviController.apply {

            }


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
                if (navController.currentBackStack.value.size > 2) {
                    if (navController.currentBackStack.value.last().destination.label != "fragment_homepage") {
                        navController.navigateUp()
                    }
                } else {
                    finish()
                }
            }
        })
    }
    fun backToHomepage(){
        startProgressBar()
        supportFragmentManager.popBackStack("", FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .add(R.id.mainFragmentContainer, FragmentHomepage.newInstance())
            .addToBackStack(null)
            .commitAllowingStateLoss()

    }

    fun signOut(){
        auth.signOut()
        restartActivity()
    }
    fun goBack() {
        startProgressBar()
        supportFragmentManager.popBackStack()

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
    private fun startProgressBar(){
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
        }else{
            navController.navigate(R.id.fragmentLogin)
        }
    }

    private fun networkCheck() {
        if (isOnline(this)) {
            checkIsSignedInAndSwitchActivity()
        } else {
            showNetworkAlert()
        }
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

}