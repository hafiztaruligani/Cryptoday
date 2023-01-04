package com.hafiztaruligani.cryptoday.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinPagingDataEntity
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import java.util.*

data class CoinResponse(

    @field:SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Any? = null,

    @field:SerializedName("symbol")
    val symbol: String? = null,

    @field:SerializedName("total_volume")
    val totalVolume: Any? = null,

    @field:SerializedName("price_change_24h")
    val priceChange24h: Any? = null,

    @field:SerializedName("atl_change_percentage")
    val atlChangePercentage: Any? = null,

    @field:SerializedName("market_cap_rank")
    val marketCapRank: Int? = null,

    @field:SerializedName("roi")
    val roi: Any? = null,

    @field:SerializedName("market_cap_change_24h")
    val marketCapChange24h: Any? = null,

    @field:SerializedName("market_cap")
    val marketCap: Any? = null,

    @field:SerializedName("ath")
    val ath: Any? = null,

    @field:SerializedName("high_24h")
    val high24h: Any? = null,

    @field:SerializedName("atl")
    val atl: Any? = null,

    @field:SerializedName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Any? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("ath_change_percentage")
    val athChangePercentage: Any? = null,

	/*@field:SerializedName("ath_date")
	val athDate: String? = null,*/

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("circulating_supply")
    val circulatingSupply: Any? = null,

    @field:SerializedName("last_updated")
    val lastUpdated: String? = null,

    @field:SerializedName("total_supply")
    val totalSupply: Any? = null,

    @field:SerializedName("fully_diluted_valuation")
    val fullyDilutedValuation: Any? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("low_24h")
    val low24h: Any? = null,

    @field:SerializedName("max_supply")
    val maxSupply: Any? = null,

    @field:SerializedName("current_price")
    val currentPrice: Any? = null,

    var favourite: Boolean = false
) {

    fun toCoinEntity(coinId: String = "", currencyPair: String): CoinPagingDataEntity {
        return CoinPagingDataEntity(
            coinId = coinId.ifBlank { id ?: "" },
            symbol = symbol ?: "",
            name = name ?: "",
            image = image ?: "",
            marketData = getMarketData(currencyPair),
            isFavorite = favourite
        )
    }

    fun getMarketData(currencyPair: String): MarketData {
        return MarketData(
            timeUnit = "24h", // TODO: declare time unit (24h, 1h, ...)
            marketCapRank = marketCapRank,
            currentPrice = currentPrice?.toString()?.toBigDecimal() ?: (0).toBigDecimal(),
            priceChangePercentage = marketCapChangePercentage24h?.toString()?.toDouble() ?: 0.0,
            low = low24h?.toString()?.toDouble() ?: 0.0,
            high = high24h?.toString()?.toDouble() ?: 0.0,
            marketCap = marketCap?.toString()?.toDouble() ?: 0.0,
            circulatingSupply = circulatingSupply?.toString()?.toDouble() ?: 0.0,
            maxSupply = maxSupply?.toString()?.toDouble() ?: 0.0,
            volume = totalVolume?.toString()?.toDouble() ?: 0.0,
            lastUpdate = "", // SimpleDateFormat("hh:mm:ss", Locale.US).format(Date()),
            currencyPair = currencyPair
        )
    }
}
