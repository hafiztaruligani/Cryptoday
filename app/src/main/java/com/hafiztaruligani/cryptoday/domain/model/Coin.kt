package com.hafiztaruligani.cryptoday.domain.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val marketData: MarketData
) : Parcelable {

    fun getTitle() =
        if (marketData.marketCapRank==null) "$name (${symbol.uppercase()})"
        else "#${marketData.marketCapRank} $name (${symbol.uppercase()})"
}
