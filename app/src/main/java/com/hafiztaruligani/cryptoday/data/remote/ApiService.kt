package com.hafiztaruligani.cryptoday.data.remote

import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.data.remote.dto.coindetail.CoinDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("coins/markets")
    suspend fun getCoinList(
        @Query("vs_currency") vsCurrency: String,
        @Query("order") order: String,
        @Query("per_page") pageSize: Int,
        @Query("page") page: Int
    ): List<CoinResponse>

    @GET("coins/{coinId}")
    suspend fun getCoinDetail(
        @Path("coinId") coinId: String,
        @Query("vs_currency") vsCurrency: String = "USD"

        ): CoinDetailResponse
}
