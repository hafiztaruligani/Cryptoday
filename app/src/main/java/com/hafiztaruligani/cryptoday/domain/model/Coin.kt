package com.hafiztaruligani.cryptoday.domain.model

import android.os.Parcelable
import com.hafiztaruligani.cryptoday.data.local.room.entity.FavouriteCoinEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val marketData: MarketData,
    var isFavorite: Boolean = false
) : Parcelable {

    constructor() : this(
        id = "",
        symbol = "",
        name = "",
        image = "",
        marketData = MarketData(),
        isFavorite = false,
    )

    fun getTitle() =
        if (marketData.marketCapRank == null) "$name (${symbol.uppercase()})"
        else "#${marketData.marketCapRank} $name (${symbol.uppercase()})"

    fun toFavouriteCoinEntity() =
        FavouriteCoinEntity(
            coinId = id,
            symbol = symbol,
            name = name,
            image = image,
            marketData = marketData,
            rank = marketData.marketCapRank,
            isFavorite = isFavorite
        )
}
