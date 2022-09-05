package com.hafiztaruligani.cryptoday.util

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.util.Cons.TAG

class CoinDiffUtil() :DiffUtil.ItemCallback<Coin>()  {
    override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean {
        oldItem.marketData.lastUpdate = ""
        newItem.marketData.lastUpdate = ""
        if (oldItem.marketData != newItem.marketData) {
            Log.d(
                "TAG",
                "areContentsTheSame ${newItem.id}: \n old: ${oldItem.marketData} \n new: ${newItem.marketData}"
            )
        }

        return oldItem.marketData == newItem.marketData
    }

}