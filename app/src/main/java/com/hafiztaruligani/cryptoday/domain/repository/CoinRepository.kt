package com.hafiztaruligani.cryptoday.domain.repository

import androidx.paging.PagingSource
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinRemoteKey
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.usecase.CoinsOrder
import com.hafiztaruligani.cryptoday.util.Resource
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
     fun getCoinsPaged(coinsOrder: CoinsOrder): PagingSource<Int,CoinEntity>
     suspend fun getCoinWithDetail(coinId: String): Flow<CoinWithDetailEntity>

     /**
      *valid pageSize range -> 1..250
      *ids size max 50
      * **/
     suspend fun getCoinsFromNetwork(
          page: Int,
          pageSize: Int,
          vsCurrencies: String,
          sortBy: String,
          ids: List<String>
     ): List<CoinResponse>

     suspend fun searchCoinId(params: String): List<String>

     suspend fun insertCoins(value: List<CoinEntity>)
     suspend fun deleteCoins()
     //Coin Remote Key Repository
     suspend fun getCoinRemoteKey(): CoinRemoteKey
     suspend fun insertCoinRemoteKey(value: CoinRemoteKey)

     suspend fun deleteCoinRemoteKey()

     suspend fun insertPair()
     fun getCurrencyPair(): Flow<List<String>>

}