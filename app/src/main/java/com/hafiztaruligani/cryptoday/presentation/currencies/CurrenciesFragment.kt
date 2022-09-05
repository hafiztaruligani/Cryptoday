package com.hafiztaruligani.cryptoday.presentation.currencies

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.hafiztaruligani.cryptoday.databinding.FragmentCurrenciesBinding
import com.hafiztaruligani.cryptoday.presentation.adapters.FrameViewPagerAdapter
import com.hafiztaruligani.cryptoday.presentation.currencies.coinslist.CoinsListFragment
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.Job

class CurrenciesFragment : Fragment() {

    companion object {
        fun newInstance() = CurrenciesFragment()
    }

    private val allCoinsListFragment = CoinsListFragment.newInstance()
    private val favouriteCoinsListFragment = CoinsListFragment.newInstance()


    lateinit var tabLayout : TabLayout
    lateinit var viewPager : ViewPager2


    private val viewModel by activityViewModels<CurrenciesViewModel>()
    private lateinit var binding: FragmentCurrenciesBinding

    lateinit var updateJob : Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCurrenciesBinding.inflate(layoutInflater)

        /*val a = LinearLayoutManager(context)
        val b = LinearLayoutManager(context)*/


        tabLayoutNavigator()
        viewPagerNavigator()

        return binding.root
    }

    private fun tabLayoutNavigator() {
        tabLayout = binding.tabLayout
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        tabLayout.addTab(tabLayout.newTab().setText("Favourite"))
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { id->
                    viewPager.currentItem = id
                    /*tab.view.setOnClickListener {
                        when(id){
                            0 && tabLayout.curren ->  a.scrollToPosition(0)
                            1 ->  b.scrollToPosition(0)
                        }
                    }*/
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun viewPagerNavigator() {
        viewPager = binding.viewPagerList
        viewPager.adapter =
            activity?.lifecycle?.let { parentLifecycle->
                FrameViewPagerAdapter(parentFragmentManager, parentLifecycle)
                    .also { adapter->
                        adapter.addView(allCoinsListFragment)
                        adapter.addView(favouriteCoinsListFragment)
                    }
            }

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.setScrollPosition(position,0f,true)
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }




}