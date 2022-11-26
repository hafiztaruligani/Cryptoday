package com.hafiztaruligani.cryptoday.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hafiztaruligani.cryptoday.R
import com.hafiztaruligani.cryptoday.databinding.ItemCoinBinding
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.util.CoinDiffUtil
import com.hafiztaruligani.cryptoday.util.glide

class CoinsAdapter(
    coinDiffUtil: CoinDiffUtil,
    private val coinDetailClick: (Coin, View) -> Unit,
    private val favouriteClick: ((Coin) -> Boolean)
) :
    PagingDataAdapter<Coin, CoinsAdapter.CoinViewHolder>(coinDiffUtil) {

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

            coinData?.marketData?.priceChangePercentage?.let {
                if (it >= 0) {
                    iconArrow.glide(root.context, R.drawable.ic_baseline_arrow_drop_up_24)
                    priceChangePercentage.setTextColor(ContextCompat.getColor(root.context, R.color.up))
                } else {
                    iconArrow.glide(root.context, R.drawable.ic_baseline_arrow_drop_down_24)
                    priceChangePercentage.setTextColor(ContextCompat.getColor(root.context, R.color.down))
                }
            }

            priceChangePercentage.text = marketData?.percentageFormatter(
                marketData.priceChangePercentage
            )?.apply { if (length > 1) replace("-", "") }

            coinData?.id?.let {

                setFavourite(btnFavourite, coinData.isFavorite)

                btnFavourite.setOnClickListener {
                    coinData.isFavorite = !coinData.isFavorite

                    val isSuccess = favouriteClick.invoke(coinData)
                    if (isSuccess) setFavourite(btnFavourite, coinData.isFavorite)
                    else coinData.isFavorite = !coinData.isFavorite
                }
            }

            root.setOnClickListener {
                coinData?.id?.let {
                    coinDetailClick.invoke(coinData, coinItem)
                }
            }
        }
    }

    private fun setFavourite(btnFavourite: ImageView, favourite: Boolean) {
        if (favourite) btnFavourite.glide(btnFavourite.context, R.drawable.ic_favourite_on)
        else btnFavourite.glide(btnFavourite.context, R.drawable.ic_favourite_off)
    }
}
