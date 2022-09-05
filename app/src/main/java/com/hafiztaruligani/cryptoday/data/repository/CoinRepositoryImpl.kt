package com.hafiztaruligani.cryptoday.data.repository

import android.util.Log
import androidx.paging.*
import com.hafiztaruligani.cryptoday.data.local.CoinDao
import com.hafiztaruligani.cryptoday.data.local.CoinRemoteKeyDao
import com.hafiztaruligani.cryptoday.data.local.entity.CoinEntity
import com.hafiztaruligani.cryptoday.data.local.entity.CoinRemoteKey
import com.hafiztaruligani.cryptoday.data.local.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.data.remote.ApiService
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.data.remote.dto.coindetail.CoinDetailResponse
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Cons
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalPagingApi::class)
class CoinRepositoryImpl (
    private val apiService: ApiService,
    private val coinDao: CoinDao,
    private val coinRemoteKeyDao: CoinRemoteKeyDao
): CoinRepository {

    override fun getCoinsPaged(): PagingSource<Int, CoinEntity> = coinDao.getAllCoins()

    override suspend fun getCoinWithDetail(coinId: String): Flow<CoinWithDetailEntity>{
        val data = apiService.getCoinDetail(coinId).toCoinDetailEntity()
        coinDao.insertCoinDetail(data)
        return coinDao.getCoinWithDetailById(coinId)
    }

    override suspend fun insertCoins(value: List<CoinEntity>) {
        coinDao.insertCoins(value)
    }

    override suspend fun deleteCoins() {
        coinDao.delete()
    }


    override suspend fun getCoinsFromNetwork(page: Int, pageSize: Int): List<CoinResponse>{
        Log.d(Cons.TAG, "load : net: $pageSize")
        return apiService.getCoinList(
            vsCurrency = "USD",
            order = "market_cap_desc",
            page = page,
            pageSize = pageSize,
            ids =""
        )
    }


    //Coin Remote Key Repository
    override suspend fun getCoinRemoteKey(): CoinRemoteKey {
        return coinRemoteKeyDao.getRemoteKey()
    }

    override suspend fun insertCoinRemoteKey(value: CoinRemoteKey) {
        coinRemoteKeyDao.insert(value)
    }

    override suspend fun deleteCoinRemoteKey() {
        coinRemoteKeyDao.delete()
    }

}