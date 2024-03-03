package com.erdince.yabancidilkelimehaznesi6.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.erdince.yabancidilkelimehaznesi6.R
import com.erdince.yabancidilkelimehaznesi6.activity.quiz.FragmentQuiz
import com.erdince.yabancidilkelimehaznesi6.databinding.FragmentLoginBinding
import com.erdince.yabancidilkelimehaznesi6.util.GoogleSavedPreference
import com.erdince.yabancidilkelimehaznesi6.util.GoogleServerClientId
import com.erdince.yabancidilkelimehaznesi6.viewmodels.DbUserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
@AndroidEntryPoint
class FragmentLogin : MainFragment() {
    private lateinit var fragmentBinding : FragmentLoginBinding
    private val binding get() = fragmentBinding
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val regCode: Int = 123
    private val auth = Firebase.auth
    private var gso: GoogleSignInOptions? = null
    private val dbUserViewModel : DbUserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentLoginBinding.inflate(inflater,container,false)
        init()
        stopProgressBar()
        return binding.root
    }
    private fun init() {
        initGoogleSignInOptions()
        initButtons()
    }

    private fun initGoogleSignInOptions() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

            .requestIdToken(GoogleServerClientId.clientId)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso!!)
    }


    private fun initButtons() {
        with(binding) {
            googleSGnButton.setOnClickListener {
                signInGoogle()
            }

            /*
            //Login with mail and pass auth. Suspended because of the personal info policies of Google
            loginButton?.setOnClickListener {

         takeInfosFromEditTexts()

         signInCheck()
     }


     signInButton?.setOnClickListener {
         switchActivity("KayitOlActivity")
     }


     forgotMyPassword?.setOnClickListener {

         switchActivity("SifreSifirlaActivity")
     }


*/
        }
    }
    /*
        private fun signInCheck() {
            if (loginEmail != "" && loginPass != "") {
                signInWithEmail()
            } else {
                makeToast("Email ve şifre alanları doldurulmalı")
            }
        }*/
    /*
        private fun signInWithEmail() {
            auth.signInWithEmailAndPassword(loginEmail, loginPass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        switchActivity("AnaEkranActivity")
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        makeToast("Email ve sifrenizi kontrol edin")
                        signInAttemptCheck()
                    }
                }
        }*/
    /*
        private fun signInAttemptCheck() {
            if (signInAttempt==6) {
                makeToast("Şifrenizi mi unuttunuz? Şifrenizi sıfırlamak için \"Şifremi Unuttum\" butonuna basabilirsiniz")
            } else {
                signInAttempt++
            }
        }

        private fun takeInfosFromEditTexts() {
            loginEmail = emailEditText?.text.toString()
            loginPass = passwordEditText?.text.toString()
        }
    */



    private fun signInGoogle() {

        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, regCode)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == regCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }


    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                GoogleSavedPreference.setEmail(requireContext(), account.email.toString())
                GoogleSavedPreference.setUsername(requireContext(), account.displayName.toString())
                dbUserViewModel.createUserData(account.email.toString(),account.displayName.toString(),auth.uid.toString(), authMethod = "Google")
                navigateWithCleaningLastBackStack(findNavController(), R.id.action_fragmentLogin_to_fragmentHomepage)

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentLogin().apply {
                arguments = Bundle().apply {

                }
            }
    }


}