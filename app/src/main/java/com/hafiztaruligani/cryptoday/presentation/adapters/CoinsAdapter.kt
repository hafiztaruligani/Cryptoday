package com.hafiztaruligani.cryptoday.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hafiztaruligani.cryptoday.databinding.ItemCoinBinding
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.util.CoinDiffUtil
import com.hafiztaruligani.cryptoday.util.Cons.TAG

class CoinsAdapter (coinDiffUtil: CoinDiffUtil)
    :PagingDataAdapter<Coin, CoinsAdapter.CoinViewHolder>(coinDiffUtil) {

    var onClickListener: ((Coin, View) -> Unit)? = null

    inner class CoinViewHolder(val binding: ItemCoinBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        return CoinViewHolder(
            ItemCoinBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")

        val coinData = getItem(position)
        val marketData = coinData?.marketData
        Log.d(TAG, "onBindViewHolder: $coinData")
        holder.binding.apply {

            ViewCompat.setTransitionName(coinItem, "${position}_${coinData?.id}")
            //setTransitionName(this, coinData,position)

            Glide.with(root.context)
                .load(coinData?.image)
                .override(logo.width, logo.height)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(logo)

            name.text = coinData?.getTitle()
            currentPrice.text = marketData?.fiatFormat(marketData.currentPrice)

            priceChangePercentage.text = marketData?.percentageFormatter(
                marketData.priceChangePercentage
            )


            root.setOnClickListener {
                coinData?.id?.let { id->
                    /*val extras = FragmentNavigatorExtras(coinItem to id)
                    val action = CurrenciesFragmentDirections.actionCurrenciesFragmentToDetailFragment(coinData)
                    Navigation.findNavController(coinItem).navigate(
                        directions = action,
                        navigatorExtras = extras
                    )*/
                    onClickListener?.invoke(coinData, coinItem)
                }
            }
        }


    }

    private fun setTransitionName(binding: ItemCoinBinding, coinData: Coin?, position: Int) {
        coinData?.let {
            binding.apply {
                ViewCompat.setTransitionName(logo, "logo$position")
                ViewCompat.setTransitionName(name, "name$position")
                ViewCompat.setTransitionName(currentPrice, "currentPrice$position")
                ViewCompat.setTransitionName(textView2, "textView2$position")
                ViewCompat.setTransitionName(priceChangePercentage, "priceChangePercentage$position")
                ViewCompat.setTransitionName(btnFavourite, "btnFavourite$position")
                ViewCompat.setTransitionName(bottomLine, "bottomLine$position")
            }
        }
    }


}
