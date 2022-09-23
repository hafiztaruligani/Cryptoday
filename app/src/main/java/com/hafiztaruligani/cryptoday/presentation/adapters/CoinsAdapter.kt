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
        val coinData = getItem(position)
        val marketData = coinData?.marketData
        holder.binding.apply {

            ViewCompat.setTransitionName(coinItem, "${position}_${coinData?.id}")

            Glide.with(root.context)
                .load(coinData?.image)
                .override(logo.width, logo.height)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(logo)

            name.text = coinData?.getTitle()
            name.isSelected = true

            currentPrice.text = marketData?.fiatFormat(marketData.currentPrice)

            priceChangePercentage.text = marketData?.percentageFormatter(
                marketData.priceChangePercentage
            )

            btnFavourite.setOnClickListener { Log.d(TAG, "btnfavo: clicked") }

            root.setOnClickListener {
                coinData?.id?.let {
                    onClickListener?.invoke(coinData, coinItem)
                }
            }
        }
    }

}
