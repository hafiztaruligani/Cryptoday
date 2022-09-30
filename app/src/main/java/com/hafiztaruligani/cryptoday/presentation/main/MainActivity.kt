package com.hafiztaruligani.cryptoday.presentation.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hafiztaruligani.cryptoday.BuildConfig
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.data.local.room.entity.FavouriteCoinEntity
import com.hafiztaruligani.cryptoday.data.repository.CoinRepositoryImpl
import com.hafiztaruligani.cryptoday.databinding.ActivityMainBinding
import com.hafiztaruligani.cryptoday.presentation.adapters.FrameViewPagerAdapter
import com.hafiztaruligani.cryptoday.presentation.convert.ConvertFragment
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesFragment
import com.hafiztaruligani.cryptoday.presentation.info.InfoFragment
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.io.IOException


/*
  CoinGecko API = max 50 REQUEST/minute
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val menuFragments = listOf(
        CurrenciesFragment.newInstance(),
        ConvertFragment.newInstance(),
        InfoFragment.newInstance()
    )

    val viewModel by viewModels<MainViewModel>()
    lateinit var binding: ActivityMainBinding
    lateinit var viewPager: ViewPager2

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleLoginRequest : ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        oneTapClient = Identity.getSignInClient(this)
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
                viewModel.setUserName(credential.id)
                Toast.makeText(this, getString(R.string.login_successfully), Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_LONG).show()
            }
        }


        supportActionBar?.hide()
        viewModel.uiState.observe(this) {
            // TODO: Loading splashscreen
            if(it.data == true) setupBottomNav()
            if(it.error.isNotBlank()){
                Toast.makeText(this, it.error, Toast.LENGTH_LONG).show()
                finish()
            }
        }

        /*fragmentViewPager()
        fragmentNavigator()*/
    }

    private fun setupBottomNav() {
        val navController = this@MainActivity.findNavController(R.id.fragment_container_nav_host)

        binding.apply {

            bottomNav.setupWithNavController(navController)

            navController.addOnDestinationChangedListener{controller, destination, arguments->
                when(destination.id){
                    R.id.currencies_fragment, R.id.convert_fragment, R.id.info_fragment -> bottomNav.isVisible = true
                    else -> bottomNav.isVisible = false
                }
            }
        }
    }

    private fun fragmentViewPager(){
        viewPager = binding.viewPagerFrame
        viewPager.adapter = FrameViewPagerAdapter(supportFragmentManager, lifecycle)
            .also { adapter->
                menuFragments.forEach { adapter.addView(it) }
            }
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun fragmentNavigator() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0 -> binding.bottomNav.selectedItemId = R.id.currencies_fragment
                    1 -> binding.bottomNav.selectedItemId = R.id.convert_fragment
                    2 -> binding.bottomNav.selectedItemId = R.id.info_fragment
                }
            }
        })

        binding.apply {
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.currencies_fragment -> {
                        viewPager.currentItem = 0
                        true
                    }
                    R.id.convert_fragment -> {
                        viewPager.currentItem = 1
                        true
                    }
                    R.id.info_fragment -> {
                        viewPager.currentItem = 2
                        true
                    }
                    else -> true
                }
            }
        }
    }

    fun login(){
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener {
            googleLoginRequest.launch(IntentSenderRequest.Builder(it.pendingIntent.intentSender).build())
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.google_account_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    fun logout() {
        viewModel.logout()
    }

}

