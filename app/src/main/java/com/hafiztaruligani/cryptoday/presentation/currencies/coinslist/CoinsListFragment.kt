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
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.presentation.adapters.CoinsAdapter
import com.hafiztaruligani.cryptoday.presentation.adapters.FavouriteCoinsAdapter
import com.hafiztaruligani.cryptoday.presentation.adapters.PagingLoadStateAdapter
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesFragmentDirections
import com.hafiztaruligani.cryptoday.presentation.currencies.CurrenciesViewModel
import com.hafiztaruligani.cryptoday.presentation.main.MainActivity
import com.hafiztaruligani.cryptoday.util.CoinDiffUtil
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest


class CoinsListFragment() : Fragment() {

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
    private var listType: ListType? = null

    private lateinit var coinsRc : RecyclerView
    private lateinit var pagingAdapter : CoinsAdapter
    private lateinit var rcAdapter: FavouriteCoinsAdapter
    private lateinit var layoutManager : LinearLayoutManager

    private val coinDiffUtil = CoinDiffUtil()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        postponeEnterTransition()
        binding = FragmentCoinsListBinding.inflate(layoutInflater)

        listType = arguments?.getParcelable<ListType>(listTypeArgs)

        coinsRc = binding.coinsRc
        layoutManager = LinearLayoutManager(context)
        coinsRc.layoutManager = layoutManager
        Log.d(TAG, "listype: $listType")
        when(listType){
            ListType.ALL -> setupPaging()
            ListType.FAVOURITE -> setupRc()
            null -> findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(listType){
            ListType.ALL -> observeAllCoins()
            ListType.FAVOURITE -> observeFavouriteCoins()
            null -> findNavController().popBackStack()
        }

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setupRc() {
        rcAdapter = FavouriteCoinsAdapter(coinDiffUtil,::navigateToDetail, viewModel::favouriteEvent)
        coinsRc.adapter = rcAdapter
    }

    private fun setupPaging() {
        pagingAdapter = CoinsAdapter(coinDiffUtil, ::navigateToDetail, viewModel::favouriteEvent)

        val pagingLoadStateAdapter= PagingLoadStateAdapter(lifecycleScope){ pagingAdapter.retry() }
        coinsRc.adapter = pagingAdapter.withLoadStateFooter(pagingLoadStateAdapter)
        binding.btnRetry.setOnClickListener { pagingAdapter.retry() }

        /*

        var initialLoad = true
        var notScrolled = false
coinsRc.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
            pagingAdapter.loadStateFlow.collect(){ loadStates ->
                pagingLoadStateAdapter.loadState = loadStates.mediator?.refresh ?:loadStates.append

                val isError = loadStates.mediator?.refresh is LoadState.Error

                binding.apply {
                    message.isVisible = isError
                    btnRetry.isVisible = isError
                    messageBackground.isVisible = isError
                    messageBackground.bringToFront()
                    message.bringToFront()
                    btnRetry.bringToFront()

                    if(pagingAdapter.itemCount==0) message.text = context?.getString(R.string.network_unavailable)
                    else message.text = context?.getString(R.string.this_data_is_not_up_to_date)

                    if (loadStates.source.refresh is LoadState.NotLoading && loadStates.append.endOfPaginationReached && pagingAdapter.itemCount < 1) {
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

    private fun observeFavouriteCoins() {
        lifecycleScope.launchWhenResumed {
            viewModel.favouriteCoins.collectLatest {
                binding.apply {
                    coinsRc.isVisible = it.isNotEmpty()
                    coinNotFound.isVisible = it.isEmpty()
                }
                rcAdapter.list.submitList(it)
            }
        }
    }

    private fun observeAllCoins() {
        lifecycleScope.launchWhenResumed{

            launch {
                viewModel.coins.collectLatest {
                    pagingAdapter.submitData(it)
                }
            }
            var needToScroll = true
            pagingAdapter.addOnPagesUpdatedListener {
                if(needToScroll && pagingAdapter.itemCount > 0){
                    binding.coinsRc.scrollToPosition(0)
                    viewModel.alreadyScroll()
                }
            }

            viewModel.needToScroll.collectLatest {
                needToScroll = it
            }

        }
    }

    private fun navigateToDetail(coinData: Coin, viewItem: View) {
        val extras = FragmentNavigatorExtras(viewItem to coinData.id)
        val action = CurrenciesFragmentDirections.actionCurrenciesFragmentToDetailFragment(coinData)
        findNavController().navigate(
            directions = action,
            navigatorExtras = extras
        )
    }

    fun scrollToTop(){
        coinsRc.smoothScrollToPosition(0)
    }
}