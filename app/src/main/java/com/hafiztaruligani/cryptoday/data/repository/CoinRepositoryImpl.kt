package com.hafiztaruligani.cryptoday.data.repository

import android.util.Log
import androidx.paging.*
import com.hafiztaruligani.cryptoday.data.local.datastore.DataStoreHelper
import com.hafiztaruligani.cryptoday.data.local.room.CoinDao
import com.hafiztaruligani.cryptoday.data.local.room.CoinRemoteKeyDao
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinRemoteKey
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.data.remote.ApiService
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.usecase.CoinsOrder
import com.hafiztaruligani.cryptoday.domain.usecase.SortBy
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.convertIntoList
import com.hafiztaruligani.cryptoday.util.removeBracket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


@OptIn(ExperimentalPagingApi::class)
class CoinRepositoryImpl(
    private val apiService: ApiService,
    private val coinDao: CoinDao,
    private val coinRemoteKeyDao: CoinRemoteKeyDao,
    private val dataStoreHelper: DataStoreHelper
) : CoinRepository {

    companion object{
        private const val SUPPORTED_PAIR_KEY = "supported_pair_key"
    }

    override fun getCoinsPaged(coinsOrder: CoinsOrder): PagingSource<Int, CoinEntity> {
        Log.d("TAG", "getCoinsPaged: $coinsOrder")
        if (coinsOrder.ids.isEmpty() || coinsOrder.ids.first().isBlank())
        return coinDao.getAllCoins()
        return coinDao.getAllCoinsWithParams(coinsOrder.params)
        // TODO: return sesuai ordernya
    }

    override suspend fun getCoin(coinId: String, currencyPair: String): Flow<CoinEntity> {
        val data = getCoinsFromNetwork(1,1,currencyPair,SortBy.MARKET_CAP_DESC().apiString, listOf(coinId))
        coinDao.insertCoins(data.map { it.toCoinEntity(currencyPair = currencyPair) })
        return coinDao.getCoinById(coinId)
    }

    override suspend fun getCoinWithDetail(coinId: String): Flow<CoinWithDetailEntity> {
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


    override suspend fun getCoinsFromNetwork(
        page: Int,
        pageSize: Int,
        vsCurrencies: String,
        sortBy: String,
        ids: List<String>
    ): List<CoinResponse> {
        Log.d(Cons.TAG, "load : net: $pageSize")
        return apiService.getCoinList(
            vsCurrency = vsCurrencies,
            order = sortBy,
            page = page,
            pageSize = pageSize,
            ids = ids.take(50).removeBracket()
        )
    }

    override suspend fun searchCoinId(params: String): List<CoinSimple> {
        val result = mutableListOf<CoinSimple>()
        val apiResult = apiService.search(params)
        apiResult.coins?.forEach {
            if(it?.thumb != null && it.id != null){
                result.add(
                    CoinSimple(
                        id = it.id,
                        logo = it.thumb,
                        marketCapRank = it.marketCapRank?:9999,
                        name = it.name?:it.id
                    )
                )
            }
        }
        return result
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

    override suspend fun insertPair(){
        val value = apiService.getSupportedPair()
        dataStoreHelper.write(SUPPORTED_PAIR_KEY, value.toString())
    }

    override fun getCurrencyPair(): Flow<List<String>> {
        return dataStoreHelper.read(SUPPORTED_PAIR_KEY, String::class.java).map { it.convertIntoList() }
    }

}