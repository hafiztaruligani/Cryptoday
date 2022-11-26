package com.hafiztaruligani.cryptoday.presentation.currencies

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.hafiztaruligani.cryptoday.databinding.FragmentCurrenciesBinding
import com.hafiztaruligani.cryptoday.presentation.adapters.FrameViewPagerAdapter
import com.hafiztaruligani.cryptoday.presentation.currencies.coinslist.CoinsListFragment
import com.hafiztaruligani.cryptoday.presentation.currencies.coinslist.ListType
import com.hafiztaruligani.cryptoday.presentation.currencies.settings.SettingsFragment
import com.hafiztaruligani.cryptoday.presentation.main.MainActivity

class CurrenciesFragment : Fragment() {

    private val TAG = javaClass.simpleName

    companion object {
        fun newInstance() = CurrenciesFragment()
        private val allCoinsListFragment = CoinsListFragment.newInstance(ListType.ALL)
        private val favoriteCoinsListFragment = CoinsListFragment.newInstance(ListType.FAVORITE)
        private val settingsFragment = SettingsFragment()
    }

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val viewModel by activityViewModels<CurrenciesViewModel>()
    private lateinit var binding: FragmentCurrenciesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrenciesBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        // setup tabLayout with viewPager
        tabLayoutNavigator()
        viewPagerNavigator()

        binding.apply {
            viewModel = this@CurrenciesFragment.viewModel
            btnSettings.setOnClickListener {
                settingsFragment.show(parentFragmentManager, "SETTINGS_TAG")
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        bindData()
    }

    private fun tabLayoutNavigator() {
        tabLayout = binding.tabLayout
        val tabAll = tabLayout.newTab().setText("All")
        val tabFavourite = tabLayout.newTab().setText("Favourite")
        tabLayout.addTab(tabAll)
        tabLayout.addTab(tabFavourite)
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)

        tabAll.view.setOnClickListener {
            if (viewPager.currentItem == 0)
                allCoinsListFragment.scrollToTop()
            else
                viewPager.currentItem = 0
        }
        tabFavourite.view.setOnClickListener {
            if (viewPager.currentItem == 1)
                favoriteCoinsListFragment.scrollToTop()
            else
                viewPager.currentItem = 1
        }
    }

    private fun viewPagerNavigator() {
        viewPager = binding.viewPagerList
        viewPager.adapter =
            activity?.lifecycle?.let { parentLifecycle ->
                FrameViewPagerAdapter(parentFragmentManager, parentLifecycle)
                    .also { adapter ->
                        adapter.addView(allCoinsListFragment)
                        adapter.addView(favoriteCoinsListFragment)
                    }
            }

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.setScrollPosition(position, 0f, true)
            }
        })
    }

    private fun bindData() {
        lifecycleScope.launchWhenResumed {
            viewModel.currenciesUiState.collect {
                binding.searchImage.isVisible = !it.loading
                binding.searchLoading.isVisible = it.loading

                if (it.needLogin) {
                    (activity as MainActivity).login()
                    viewModel.needLogin(false)
                }
            }
        }
    }
}
