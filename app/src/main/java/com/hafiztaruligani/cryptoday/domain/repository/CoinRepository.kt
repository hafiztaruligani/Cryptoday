package com.hafiztaruligani.cryptoday.domain.repository

import androidx.paging.PagingData
import com.hafiztaruligani.cryptoday.data.local.room.entity.*
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    // coin paging
    suspend fun getCoinsPaged(coinsOrder: CoinsOrder): Flow<PagingData<CoinPagingDataEntity>>
    suspend fun getCoinWithDetail(coinId: String): Flow<CoinWithDetailEntity>

    /**
     *valid pageSize range -> 1..250
     *ids size max 50
     * **/
    suspend fun getCoinsRemote(
        page: Int,
        pageSize: Int,
        vsCurrencies: String,
        sortBy: String,
        ids: List<String>
    ): List<CoinResponse>

    suspend fun searchCoinId(query: String): List<CoinSimple>

    suspend fun getCoin(coinId: String, currencyPair: String): Flow<CoinPagingDataEntity>

    suspend fun insertCoins(value: List<CoinPagingDataEntity>)
    suspend fun deleteCoins()

    // query history
    suspend fun insertQueryHistory(query: String)
    suspend fun getQueryHistory(): Flow<List<QueryHistoryEntity?>?>?

    // Coin Remote Key Repository
    suspend fun getCoinRemoteKey(remoteKeyId: String): RemoteKey?
    suspend fun insertCoinRemoteKey(value: RemoteKey)
    suspend fun deleteCoinRemoteKey(remoteKeyId: String)

    // pair currency
    suspend fun insertPair()
    fun getCurrencyPair(): Flow<List<String>>

    // favorite
    fun getFavourite(coinsOrder: CoinsOrder): Flow<List<FavouriteCoinEntity>>
    suspend fun addFavourite(coin: Coin)
    suspend fun deleteFavouriteById(coinId: String)
    fun deleteFavourites()

    // update paging market data
    suspend fun updateMarketData(coinId: String, marketData: MarketData)
}
