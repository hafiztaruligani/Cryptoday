package com.hafiztaruligani.cryptoday.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FrameViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = mutableListOf<Fragment>()
    fun addView(view: Fragment) = fragments.add(view)

    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
}
