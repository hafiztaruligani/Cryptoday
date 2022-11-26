package com.hafiztaruligani.cryptoday.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.model.MarketData

@Entity(tableName = "favourite")
data class FavouriteCoinEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "coin_id")
    val coinId: String,
    @ColumnInfo(name = "symbol")
    val symbol: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "market_data")
    val marketData: MarketData,
    @ColumnInfo(name = "rank")
    val rank: Int? = marketData.marketCapRank,
    @ColumnInfo(name = "favourite")
    val isFavorite: Boolean
) {
    fun toCoin(): Coin = Coin(
        id = coinId,
        symbol = symbol,
        name = name,
        image = image,
        marketData = marketData,
        isFavorite = isFavorite
    )
}
