package com.hafiztaruligani.cryptoday.presentation.info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.FragmentInfoBinding
import com.hafiztaruligani.cryptoday.presentation.adapters.CoinsAdapter
import com.hafiztaruligani.cryptoday.presentation.main.MainActivity
import com.hafiztaruligani.cryptoday.util.CoinDiffUtil
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InfoFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFragment().also {

            Log.d(TAG, "onCreateView: fragment info")
        }
    }

    private val viewModel by viewModels<InfoViewModel>()

    private lateinit var binding: FragmentInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(layoutInflater)

        viewLifecycleOwner.lifecycleScope.launch() {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.description.collectLatest {
                        binding.description.text = it
                    }
                }

                viewModel.userName.collectLatest {
                    binding.apply {
                        if (it.isBlank()) {
                            userName.text = resources.getText(R.string.cryptoday)
                            btnLoginLogout.text = resources.getText(R.string.login)
                            btnLoginLogout.setOnClickListener { (activity as MainActivity).login() }
                        } else {
                            userName.text = it
                            btnLoginLogout.text = resources.getText(R.string.logout)
                            btnLoginLogout.setOnClickListener { (activity as MainActivity).logout() }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        this@InfoFragment.startPostponedEnterTransition()
    }

    private lateinit var coinsRc: RecyclerView
    private lateinit var adapter: CoinsAdapter
    lateinit var layoutManager: LinearLayoutManager

    private val coinDiffUtil = CoinDiffUtil()
}
