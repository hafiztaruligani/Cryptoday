package com.hafiztaruligani.cryptoday.presentation.currencies.coinslist

import android.os.Build
import android.os.Bundle
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
import androidx.paging.CombinedLoadStates
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
import com.hafiztaruligani.cryptoday.util.CoinDiffUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * this fragment view have one Recycler View (RC), used to provide coin list information.
 * there is has two implementation/list type of this fragment:
 * 1. ALL = showing all coins, the RC adapter is using paging adapter
 * 2. FAVORITE = showing favorite coins, the RC adapter is using recycler view adapter
 */
class CoinsListFragment : Fragment() {

    // private val TAG = javaClass.simpleName

    companion object {
        private const val listTypeArgs = "listType"
        fun newInstance(listType: ListType): CoinsListFragment {
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

    private lateinit var coinsRc: RecyclerView
    private lateinit var pagingAdapter: CoinsAdapter
    private lateinit var rcAdapter: FavouriteCoinsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var pagingLoadStateAdapter: PagingLoadStateAdapter

    private val coinDiffUtil = CoinDiffUtil()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        postponeEnterTransition()
        binding = FragmentCoinsListBinding.inflate(layoutInflater)

        coinsRc = binding.coinsRc
        layoutManager = LinearLayoutManager(context)
        coinsRc.layoutManager = layoutManager

        setListType()
        when (listType) {
            ListType.ALL -> setupPaging()
            ListType.FAVORITE -> setupRc()
            null -> findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun setListType() {
        listType = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable(listTypeArgs, ListType::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(listTypeArgs)
        }
    }

    // only called when ListType is FAVORITE
    private fun setupRc() {
        rcAdapter = FavouriteCoinsAdapter(
            coinDiffUtil,
            ::navigateIntoDetail,
            viewModel::favouriteEvent
        )
        coinsRc.adapter = rcAdapter
    }

    // only called when ListType is ALL
    private fun setupPaging() {
        pagingAdapter = CoinsAdapter(coinDiffUtil, ::navigateIntoDetail, viewModel::favouriteEvent)

        pagingLoadStateAdapter = PagingLoadStateAdapter(lifecycleScope) {
            pagingAdapter.retry()
        }
        coinsRc.adapter = pagingAdapter.withLoadStateFooter(pagingLoadStateAdapter)
        binding.btnRetry.setOnClickListener { pagingAdapter.retry() }

        calibrateUi()
        observeLoadState()
    }

    private fun calibrateUi() {

        // This initiate variable serves as a benchmark for rc to scroll
        // up when initiating paging data, used when adapter is on update
        var initiate = false
        pagingAdapter.addOnPagesUpdatedListener {
            if (initiate) {
                coinsRc.scrollToPosition(0)
                initiate = false
            }
        }

        // set initiate variable with true
        lifecycleScope.launch {
            launch {
                pagingAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.mediator?.refresh }
                    .filter { it.mediator?.refresh is LoadState.NotLoading }
                    .collect {
                        initiate = true
                    }
            }
        }
    }

    // collecting load state to used by pagingLoadStateAdapter and error indicator
    private fun observeLoadState() {
        lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect { loadStates ->
                pagingLoadStateAdapter.loadState = loadStates.mediator?.append ?: loadStates.append
                setErrorListener(loadStates)
            }
        }
    }

    private fun setErrorListener(loadStates: CombinedLoadStates) {
        binding.apply {

            val isError = loadStates.mediator?.refresh is LoadState.Error
            message.isVisible = isError
            btnRetry.isVisible = isError
            messageBackground.isVisible = isError
            messageBackground.bringToFront()
            message.bringToFront()
            btnRetry.bringToFront()

            if (pagingAdapter.itemCount == 0) message.text =
                context?.getString(R.string.network_unavailable)
            else message.text = context?.getString(R.string.this_data_is_not_up_to_date)

            val isEmptyData: Boolean = loadStates.source.refresh is LoadState.NotLoading &&
                loadStates.append.endOfPaginationReached &&
                pagingAdapter.itemCount < 1

            coinsRc.isVisible = !isEmptyData
            coinNotFound.isVisible = isEmptyData
        }
    }

    private fun bindData() {
        lifecycleScope.launch {
            when (listType) {
                ListType.ALL -> observeAllCoins()
                ListType.FAVORITE -> observeFavouriteCoins()
                null -> findNavController().popBackStack()
            }
        }
    }

    // only called when ListType is Favorite
    private suspend fun observeFavouriteCoins() {
        viewModel.favouriteCoins.collectLatest {
            binding.apply {
                coinsRc.isVisible = it.isNotEmpty()
                coinNotFound.isVisible = it.isEmpty()
            }
            rcAdapter.list.submitList(it)
        }
    }

    // only called when ListType is ALL
    private suspend fun observeAllCoins() {
        viewModel.coinPagingData.collectLatest {
            pagingAdapter.submitData(it)
        }
    }

    // navigation into detail fragment
    private fun navigateIntoDetail(coinData: Coin, viewItem: View) {
        val extras = FragmentNavigatorExtras(viewItem to coinData.id)
        val action = CurrenciesFragmentDirections.actionCurrenciesFragmentToDetailFragment(coinData)
        findNavController().navigate(
            directions = action,
            navigatorExtras = extras
        )
    }

    // called from Currencies Fragment (parent fragment), to handle when tab item clicked
    fun scrollToTop() {
        coinsRc.smoothScrollToPosition(0)
    }
}
