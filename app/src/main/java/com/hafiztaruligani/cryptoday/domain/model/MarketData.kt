package com.hafiztaruligani.cryptoday.domain.model

import android.os.Parcelable
import android.util.Log
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarketData(
    val timeUnit: String,
    val marketCapRank: Int?,
    val currentPrice: Double,
    val priceChangePercentage: Double,
    val low: Double,
    val high: Double,
    val marketCap: Double,
    val circulatingSupply: Double,
    val maxSupply: Double,
    val volume: Double,
    val currencyPair: String,
    var lastUpdate: String
) : Parcelable {

    constructor() : this("", null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "", "")
    companion object {
        private val formatter = NumberFormat.getCurrencyInstance()
    }

    fun fiatFormat(value: Any): String {
        try {
            if (value.toString().toDouble() == 0.0) return "-"
            val currencyFormat = Currency.getInstance(currencyPair.uppercase())
            formatter.currency = currencyFormat
            formatter.maximumFractionDigits = 3
            formatter.isParseIntegerOnly = true
            val result = formatter.format(value)
            return dropZero(result)
        } catch (e: Exception) {
            return cryptoFormat(value, currencyPair)
        }
    }

    fun cryptoFormat(value: Any, symbol: String): String {
        return try {
            if (value.toString().toDouble() == 0.0) return "-"

            val formatter = DecimalFormat("###,###,###")
            val result = "${formatter.format(value)} ${symbol.uppercase()}"

            return dropZero(result)
        } catch (e: Exception) {
            "-"
        }
    }

    fun percentageFormatter(value: Any): String {
        return try {
            val formatter = DecimalFormat("###,##0.00")
            val result = "${formatter.format(value)}%"
            return dropZero(result)
        } catch (e: Exception) {
            Log.d("TAG", "currencyFormat: $e")
            "-"
        }
    }

    private fun dropZero(value: String): String {
        var result = value
        while (result.last() == '0' && result[result.length - 2] != '.') {
            result = result.dropLast(1)
        }
        return result
    }
}
