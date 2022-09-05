package com.hafiztaruligani.cryptoday.domain.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.hafiztaruligani.cryptoday.data.local.entity.CoinEntity
import com.hafiztaruligani.cryptoday.data.local.entity.CoinRemoteKey
import com.hafiztaruligani.cryptoday.data.local.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.model.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
     fun getCoinsPaged(): PagingSource<Int,CoinEntity>
     suspend fun getCoinWithDetail(coinId: String): Flow<CoinWithDetailEntity>

     /**
      *valid pageSize range -> 1..250
      * **/
     suspend fun getCoinsFromNetwork(page: Int, pageSize: Int): List<CoinResponse>
     suspend fun insertCoins(value: List<CoinEntity>)
     suspend fun deleteCoins()

     //Coin Remote Key Repository
     suspend fun getCoinRemoteKey(): CoinRemoteKey
     suspend fun insertCoinRemoteKey(value: CoinRemoteKey)
     suspend fun deleteCoinRemoteKey()

}