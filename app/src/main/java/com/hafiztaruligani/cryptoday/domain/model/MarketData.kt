package com.hafiztaruligani.cryptoday.domain.model

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize
import java.text.DecimalFormat

@Parcelize
data class MarketData(
    val timeUnit: String,
    val marketCapRank: Int,
    val currentPrice: Double,
    val priceChangePercentage: Double,
    val low: Double,
    val high: Double,
    val marketCap: Double,
    val circulatingSupply: Double,
    val maxSupply: Double,
    val pair: String,
    val volume: Double,
    var lastUpdate: String
) : Parcelable {
    fun fiatFormat(value: Any): String{
        return try {
            if(value.toString().toDouble()==0.0) return "-"

            val formatter = DecimalFormat("###,##0.000")
            val result = "$ ${formatter.format(value)}" // TODO: currencies

            return dropZero(result)
        }catch (e: Exception){
            "-"
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
