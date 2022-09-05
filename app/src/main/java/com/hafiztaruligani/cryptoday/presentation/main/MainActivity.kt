package com.hafiztaruligani.cryptoday.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.ActivityMainBinding
import com.hafiztaruligani.cryptoday.presentation.adapters.FrameViewPagerAdapter
import com.hafiztaruligani.cryptoday.presentation.convert.ConvertFragment
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesFragment
import com.hafiztaruligani.cryptoday.presentation.info.InfoFragment
import com.hafiztaruligani.cryptoday.util.Cons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupBottomNav()
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

    override fun onBackPressed() {
        super.onBackPressed()
        /*if(viewPager.currentItem==0) super.onBackPressed()
        else viewPager.currentItem-=1*/
    }
}

