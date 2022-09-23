package com.hafiztaruligani.cryptoday.presentation.currencies.coinslist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.hafiztaruligani.cryptoday.presentation.adapters.CoinsAdapter
import com.hafiztaruligani.cryptoday.presentation.adapters.PagingLoadStateAdapter
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesFragmentDirections
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesViewModel
import com.hafiztaruligani.cryptoday.util.CoinDiffUtil
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine


class CoinsListFragment() : Fragment() {

    val a = mutableListOf<String>()
    companion object {
        private const val listTypeArgs = "listType"
        fun newInstance(listType: ListType) : CoinsListFragment {
            val frag = CoinsListFragment()
            val bundle = Bundle()
            bundle.putParcelable(listTypeArgs, listType)
            frag.arguments = bundle
            return frag
        }
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

        var initialLoad = true
        var notScrolled = false
        /*adapter.addOnPagesUpdatedListener {
            if(adapter.itemCount<=0) initialLoad = true.also { Log.d(TAG, "setupRc: initial") }
            if(adapter.itemCount>0 && initialLoad)//notScrolled
                coinsRc.scrollToPosition(0).also { initialLoad = false } // TODO: scroll
        }*/

        /*coinsRc.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                notScrolled = dy == 0
            }
        })

        adapter.addLoadStateListener {
            if (it.source.refresh is LoadState.Loading)
                Log.d(TAG, "loadstatelistener: new source")

            if (it.mediator?.refresh is LoadState.Loading)
                Log.d(TAG, "loadstatelistener: new mediator/remote")
        }*/

        lifecycleScope.launch {
            adapter.loadStateFlow.collect(){ loadStates ->
                pagingLoadStateAdapter.loadState = loadStates.mediator?.refresh ?:loadStates.append

                val isError = loadStates.mediator?.refresh is LoadState.Error

                if(isError){
                    Log.d(TAG, "iserror: ${(loadStates.mediator?.refresh as LoadState.Error).error}")}

                binding.apply {
                    message.isVisible = isError
                    btnRetry.isVisible = isError
                    messageBackground.isVisible = isError
                    messageBackground.bringToFront()
                    message.bringToFront()
                    btnRetry.bringToFront()

                    if(adapter.itemCount==0) message.text = context?.getString(R.string.network_unavailable)
                    else message.text = context?.getString(R.string.this_data_is_not_up_to_date)

                    if (loadStates.source.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && adapter.itemCount < 1) {
                        coinsRc.isVisible = false
                        coinNotFound.isVisible = true
                    } else {
                        coinsRc.isVisible = true
                        coinNotFound.isVisible = false
                    }
                }

            }
        }
    }

    private fun observeData() {
        lifecycleScope.launchWhenResumed{
            val listType = arguments?.getParcelable<ListType>(listTypeArgs)
            launch {
                if (listType == ListType.ALL) viewModel.coins.collectLatest {
                        adapter.submitData(it)
                    }
            }
            viewModel.needToScroll.collectLatest {
                if(it) {
                    binding.coinsRc.scrollToPosition(0)
                    viewModel.alreadyScroll()
                }
            }
        }
    }

    fun scrollToTop(){
        coinsRc.smoothScrollToPosition(0)
    }
}