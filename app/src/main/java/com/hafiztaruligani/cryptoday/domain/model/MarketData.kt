package com.hafiztaruligani.cryptoday.domain.model

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize
import okhttp3.internal.format
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

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

    companion object{
        private val formatter = NumberFormat.getCurrencyInstance()
    }

    fun fiatFormat(value: Any): String{
        try {
            if(value.toString().toDouble()==0.0) return "-"
            val currencyFormat = Currency.getInstance(currencyPair.uppercase())
            formatter.currency = currencyFormat
            formatter.maximumFractionDigits = 3
            formatter.isParseIntegerOnly = true
            val result = formatter.format(value)
            return dropZero(result)
        }catch (e: Exception){
            return cryptoFormat(value, currencyPair)
        }
    }

    fun fiatFormat(value: Any, n:Int): String{
        if(n<1) throw Exception()
        return try {
            if(value.toString().toDouble()==0.0) return "-"

            var patern = "###,##0."
            repeat(n){ patern = patern.plus('0') }
            val formatter = DecimalFormat(patern)
            val result = "$ ${formatter.format(value)}" // TODO: currencies
            return dropZero(result)
        }catch (e: Exception){
            Log.d("TAG", "currencyFormat: $e")
            "-"
        }
    }

    fun cryptoFormat(value: Any, symbol: String): String{
        return try {
            if(value.toString().toDouble()==0.0) return "-"

            val formatter = DecimalFormat("###,###,###")
            val result = "${formatter.format(value)} ${symbol.uppercase()}" // TODO: currencies

            return dropZero(result)
        }catch (e: Exception){
            "-"
        }
    }


    fun percentageFormatter(value: Any): String{
        return try {
            val formatter = DecimalFormat("###,##0.00")
            val result = "${formatter.format(value)}%" // TODO: currencies
            return dropZero(result)
        }catch (e: Exception){
            Log.d("TAG", "currencyFormat: $e")
            "-"
        }
    }

    private fun dropZero(value: String): String{
        var result = value
        while (result.last()=='0' && result[result.length-2]!='.'){
            result = result.dropLast(1)
        }
        return result
    }

}
