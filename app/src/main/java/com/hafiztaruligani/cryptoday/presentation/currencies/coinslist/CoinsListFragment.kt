package com.hafiztaruligani.cryptoday.presentation.currencies.coinslist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.FragmentCoinsListBinding
import com.hafiztaruligani.cryptoday.presentation.LoadingBar
import com.hafiztaruligani.cryptoday.presentation.adapters.CoinsAdapter
import com.hafiztaruligani.cryptoday.presentation.adapters.PagingLoadStateAdapter
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesFragmentDirections
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesViewModel
import com.hafiztaruligani.cryptoday.util.CoinDiffUtil
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest


class CoinsListFragment : Fragment() {

    companion object {
        fun newInstance() = CoinsListFragment().also {
            Log.d(TAG, "newInstance: all ")
        }
        private lateinit var loading: LoadingBar
    }


    private val viewModel by activityViewModels<CurrenciesViewModel>()
    private lateinit var binding: FragmentCoinsListBinding

    private lateinit var coinsRc : RecyclerView
    private lateinit var adapter : CoinsAdapter
    private lateinit var layoutManager : LinearLayoutManager

    private val coinDiffUtil = CoinDiffUtil()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        postponeEnterTransition()
        binding = FragmentCoinsListBinding.inflate(layoutInflater)

        setupRc()
        loading = LoadingBar(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        adapter.onClickListener = { coinData, viewItem->
            val extras = FragmentNavigatorExtras(viewItem to coinData.id)
            val action = CurrenciesFragmentDirections.actionCurrenciesFragmentToDetailFragment(coinData)
            findNavController().navigate(
                directions = action,
                navigatorExtras = extras
            )
        }
    }


    private fun setupRc() {
        coinsRc = binding.coinsRc
        layoutManager = LinearLayoutManager(context)
        coinsRc.layoutManager = layoutManager
        adapter = CoinsAdapter(coinDiffUtil)
        val pagingLoadStateAdapter= PagingLoadStateAdapter(lifecycleScope){ adapter.retry() }
        coinsRc.adapter = adapter.withLoadStateFooter(pagingLoadStateAdapter)
        binding.btnRetry.setOnClickListener { adapter.retry() }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect(){
                pagingLoadStateAdapter.loadState = it.mediator?.refresh ?:it.append

                val isError = it.mediator?.refresh is LoadState.Error

                binding.apply {
                    messageBackground.bringToFront()
                    message.bringToFront()
                    btnRetry.bringToFront()
                    message.isVisible = isError
                    btnRetry.isVisible = isError
                    messageBackground.isVisible = isError

                    if(adapter.itemCount==0) message.text = context?.getString(R.string.network_unavailable)
                    else message.text = context?.getString(R.string.this_data_is_not_up_to_date)
                }

                when (val refresh = it.mediator?.refresh) {
                    is LoadState.Loading -> Log.d(TAG, "setupRc refresh: loading")
                    is LoadState.NotLoading -> Log.d(TAG, "setupRc refresh: not loading")
                    is LoadState.Error -> {
                        Log.d(TAG, "setupRc refresh: error : ${refresh.error}")
                        //  pagingLoadStateAdapter.loadState = loadStates.refresh
                    }
                    else -> {}
                }

                when (val append = it.mediator?.append) {
                    is LoadState.Loading -> Log.d(TAG, "setupRc append: loading")
                    is LoadState.NotLoading -> Log.d(TAG, "setupRc append: not loading")
                    is LoadState.Error -> {
                        Log.d(TAG, "setupRc append: error : ${append.error}")
                        //pagingLoadStateAdapter.loadState = append
                    }
                    else -> {}
                }
            }

        }
    }

    private fun observeData() {
        lifecycleScope.launchWhenResumed{
            viewModel.coins.collect() { data ->
                adapter.submitData(data)
            }
        }
    }
}