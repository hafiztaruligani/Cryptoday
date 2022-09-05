package com.hafiztaruligani.cryptoday.presentation.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.hafiztaruligani.cryptoday.BuildConfig
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.FragmentLoginBinding
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Login : Fragment() {

    companion object {
        fun newInstance() = Login()
    }

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleLoginRequest : ActivityResultLauncher<IntentSenderRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.FIREBASE_URL)
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true).build()

        googleLoginRequest = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
                insertData(credential)
            }catch (e: Exception){
                Toast.makeText(context, "An unexpected error occurred", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    private fun insertData(credential: SignInCredential) {
        lifecycleScope.launch() {
            viewModel.setUserName(credential.id)
            delay(1000)
            viewModel.getUserName().collect() {

                Log.d(TAG, "googlelogin: ${it}")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLoginGoogle.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest).addOnSuccessListener {
                googleLoginRequest.launch(IntentSenderRequest.Builder(it.pendingIntent.intentSender).build())
            }.addOnFailureListener {
                Toast.makeText(context, getString(R.string.google_account_not_found), Toast.LENGTH_SHORT).show()
            }
        }
    }
}