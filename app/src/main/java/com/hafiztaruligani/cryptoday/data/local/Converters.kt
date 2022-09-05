package com.hafiztaruligani.cryptoday.data.local

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.hafiztaruligani.cryptoday.domain.model.CoinDetail
import com.hafiztaruligani.cryptoday.domain.model.MarketData

@TypeConverters
class Converters {
    @TypeConverter
    fun a(value: List<Any>?) = Gson().toJson(value)

    @TypeConverter
    fun b(value: String) =
        Gson().fromJson(value, Array<Any>::class.java).toList()

    @TypeConverter
    fun c(value: CoinDetail) = Gson().toJson(value)

    @TypeConverter
    fun d(value: String) =
        Gson().fromJson(value, CoinDetail::class.java)

    @TypeConverter
    fun e(value: MarketData) = Gson().toJson(value)

    @TypeConverter
    fun f(value: String) =
        Gson().fromJson(value, MarketData::class.java)
}