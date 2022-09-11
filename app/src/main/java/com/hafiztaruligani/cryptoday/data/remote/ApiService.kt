package com.hafiztaruligani.cryptoday.data.remote

import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.data.remote.dto.coindetail.CoinDetailResponse
import com.hafiztaruligani.cryptoday.data.remote.dto.search.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /**
     * limit 50 ids
     **/
    @GET("coins/markets")
    suspend fun getCoinList(
        @Query("vs_currency") vsCurrency: String,
        @Query("order") order: String,
        @Query("per_page") pageSize: Int,
        @Query("page") page: Int,
        @Query("ids") ids: String
    ): List<CoinResponse>

    @GET("coins/{coinId}")
    suspend fun getCoinDetail(
        @Path("coinId") coinId: String,
        @Query("vs_currency") vsCurrency: String = "USD"

        ): CoinDetailResponse

    @GET("search")
    suspend fun search(
        @Query("query") params: String
    ):SearchResponse

    @GET("simple/supported_vs_currencies")
    suspend fun getSupportedPair(): List<String>
}
