package com.hafiztaruligani.cryptoday.presentation.currencies.coinslist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hafiztaruligani.cryptoday.data.local.AppDatabase
import com.hafiztaruligani.cryptoday.databinding.FragmentCoinsListBinding
import com.hafiztaruligani.cryptoday.presentation.LoadingBar
import com.hafiztaruligani.cryptoday.presentation.adapters.CoinsAdapter
import com.hafiztaruligani.cryptoday.presentation.adapters.PagingLoadStateAdapter
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesFragmentDirections
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesViewModel
import com.hafiztaruligani.cryptoday.util.CoinDiffUtil
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.Cons.UPDATE_DELAY_TIME
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import retrofit2.HttpException


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
        layoutManager = LinearLayoutManager(context)
        adapter = CoinsAdapter(coinDiffUtil)
        coinsRc = binding.coinsRc
        coinsRc.adapter = adapter
        coinsRc.layoutManager = layoutManager

        adapter.withLoadStateFooter(PagingLoadStateAdapter(adapter::retry))
       /* lifecycleScope.launchWhenResumed {
            adapter.loadStateFlow.collectLatest{
                when (val refresh = it.mediator?.refresh){
                    is LoadState.Loading ->  Log.d(TAG, "setupRc refresh: loading")
                    is LoadState.NotLoading -> Log.d(TAG, "setupRc refresh: not loading")
                    is LoadState.Error -> Log.d(TAG, "setupRc refresh: error : ${refresh.error}").also {
                        if(refresh.error is  HttpException){
                            Log.d(TAG, "setupRc: error code ${(refresh.error as HttpException).code()}")
                        }
                    }
                    else -> {}
                }

                when (val append = it.mediator?.append){
                    is LoadState.Loading ->  Log.d(TAG, "setupRc append: loading")
                    is LoadState.NotLoading -> Log.d(TAG, "setupRc append: not loading")
                    is LoadState.Error -> Log.d(TAG, "setupRc append: error : ${append.error}")
                    else -> {}
                }

            }
        }*/
    }

    private fun observeData() {
        viewModel.initData()

        viewModel.state.observe(viewLifecycleOwner){state->
            Log.e(TAG, "error log view state: a ${
                if(state.data!=null) "success"
                else if (state.error.isNotBlank()) "error ${state.error} ${state.loading}"
                else "loading :${state.loading}"
            }")

            state.data?.let {
                lifecycleScope.launchWhenResumed {
                    adapter.submitData(it)
                }
            }

            if(state.error.isNotBlank()) {
                Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
            }
            loading.state(state.loading)
        }
    }
}