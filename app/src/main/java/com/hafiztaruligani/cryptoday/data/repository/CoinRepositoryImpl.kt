package com.hafiztaruligani.cryptoday.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FirebaseFirestore
import com.hafiztaruligani.cryptoday.BuildConfig
import com.hafiztaruligani.cryptoday.data.local.datastore.DataStoreHelper
import com.hafiztaruligani.cryptoday.data.local.room.CoinDao
import com.hafiztaruligani.cryptoday.data.local.room.CoinRemoteKeyDao
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinPagingDataEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.FavouriteCoinEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.QueryHistoryEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.RemoteKey
import com.hafiztaruligani.cryptoday.data.paging.CoinPagingRemoteMediator
import com.hafiztaruligani.cryptoday.data.remote.ApiService
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder
import com.hafiztaruligani.cryptoday.domain.usecase.coin.SortBy
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.convertIntoList
import com.hafiztaruligani.cryptoday.util.removeBracket
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CoinRepositoryImpl(
    private val apiService: ApiService,
    private val coinDao: CoinDao,
    private val coinRemoteKeyDao: CoinRemoteKeyDao,
    private val dataStoreHelper: DataStoreHelper,
    private val userRepository: UserRepository,
    private val fireStore: FirebaseFirestore
) : CoinRepository {

    companion object {
        private const val SUPPORTED_PAIR_KEY = "supported_pair_key"
        private const val FAVOURITE_KEY = "FAVOURITE_KEY"
    }

    init {
        runBlocking(Dispatchers.IO) {
            try {
                val favouriteCoins = downloadFavouriteCoin()
                val pair = userRepository.getUserCurrencyPair().first()
                if (favouriteCoins.isNotEmpty()) {
                    getCoinsRemote(
                        page = 1,
                        pageSize = favouriteCoins.size,
                        vsCurrencies = pair,
                        sortBy = SortBy.MARKEY_CAP_ASC().apiString,
                        favouriteCoins
                    ).forEach {
                        val data = it.toCoinEntity(currencyPair = pair).toCoin()
                        data.isFavorite = true
                        addFavourite(data)
                    }
                } else {
                    Log.e(TAG, "repo init: user not login")
                }
            } catch (e: Exception) {
                Log.e(TAG, "repo init: ${e.message}")
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getCoinsPaged(
        coinsOrder: CoinsOrder
    ): Flow<PagingData<CoinPagingDataEntity>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = Cons.PAGE_SIZE,
                pageSize = Cons.PAGE_SIZE,
                prefetchDistance = Cons.PRE_FETCH_DISTANCE
            ),
            remoteMediator = CoinPagingRemoteMediator(this, coinsOrder)
        ) {
            if (coinsOrder.query.isBlank())
                coinDao.getCoinPagingData()
            else coinDao.getCoinPagingDataByQuery(coinsOrder.query)
        }.flow

    override suspend fun getCoin(coinId: String, currencyPair: String): Flow<CoinPagingDataEntity> {
        val remoteData = getCoinsRemote(
            1,
            1,
            currencyPair,
            SortBy.MARKEY_CAP_ASC().apiString,
            listOf(coinId)
        )
        remoteData.map { it.toCoinEntity(currencyPair = currencyPair) }.forEach {
            coinDao.updateCoinPagingData(it.coinId, it.marketData)
        }
        return coinDao.getCoinPagingDataById(coinId)
    }

    override suspend fun getCoinWithDetail(coinId: String): Flow<CoinWithDetailEntity> {
        val remoteData = apiService.getCoinDetail(coinId).toCoinDetailEntity()
        coinDao.insertCoinDetail(remoteData)
        return coinDao.getCoinWithDetailById(coinId)
    }

    override suspend fun insertCoins(value: List<CoinPagingDataEntity>) {
        coinDao.insertCoinPagingData(value)
    }

    override suspend fun deleteCoins() {
        coinDao.deleteCoinPagingData()
    }

    override suspend fun insertQueryHistory(query: String) {
        withContext(Dispatchers.IO) {
            coinDao.insertQueryHistory(
                QueryHistoryEntity(query)
            )
        }
    }

    override suspend fun getQueryHistory(): Flow<List<QueryHistoryEntity?>?>? {
        return coinDao.getQueryHistory()
    }

    override fun getFavourite(coinsOrder: CoinsOrder): Flow<List<FavouriteCoinEntity>> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favouriteList = coinDao.getAllFavourite().first().map { it.coinId }

                val newData = getCoinsRemote(
                    page = 1,
                    pageSize = favouriteList.size,
                    ids = favouriteList,
                    vsCurrencies = coinsOrder.currencyPair,
                    sortBy = coinsOrder.sortBy.apiString
                )
                newData.map {
                    it.toCoinEntity(currencyPair = coinsOrder.currencyPair)
                        .toCoin().toFavouriteCoinEntity()
                }.forEach {
                    coinDao.insertFavouriteCoin(it)
                }
            } catch (e: Exception) {
                Log.e(TAG, "getFavourite: ${e.message}")
            }
        }

        return coinDao.getAllFavourite()
    }

    override suspend fun addFavourite(coin: Coin) {
        coinDao.updateCoinFavourite(coin.id, true)
        coinDao.insertFavouriteCoin(coin.toFavouriteCoinEntity())
        uploadFavouriteCoins(
            coinDao.getAllFavourite().first()
        )
    }

    override suspend fun deleteFavouriteById(coinId: String) {
        coinDao.updateCoinFavourite(coinId, false)
        coinDao.deleteFavouriteById(coinId)
    }

    override fun deleteFavourites() {
        CoroutineScope(Dispatchers.IO).launch {
            coinDao.getAllFavourite().first().forEach {
                coinDao.updateCoinFavourite(it.coinId, false)
            }
            coinDao.deleteFavourite()
        }
    }

    private suspend fun uploadFavouriteCoins(coins: List<FavouriteCoinEntity>) {
        try {
            val userKey = userRepository.getUserName().first()
            if (userKey.isNotEmpty()) {
                val data = coins.map { coins -> coins.coinId }.toString()
                fireStore.collection(BuildConfig.FIRESTORE_COLLECTION).document(userKey)
                    .set(hashMapOf(FAVOURITE_KEY to data))
                    .addOnFailureListener { e ->
                        throw (e)
                    }
            } else throw (IOException("user not login"))
        } catch (e: Exception) {
            Log.e(TAG, "uploadFavouriteCoins: ${e.message}")
        }
    }

    private suspend fun downloadFavouriteCoin(): List<String> {
        return try {
            val userKey = userRepository.getUserName().first()
            if (userKey.isNotEmpty()) {
                val result = fireStore
                    .collection(BuildConfig.FIRESTORE_COLLECTION)
                    .document(userKey)
                    .get().await()
                val data = result[FAVOURITE_KEY]
                (data as String).convertIntoList()
            } else throw (IOException("user not login"))
        } catch (e: Exception) {
            Log.e(TAG, "uploadFavouriteCoins: ${e.message}")
            listOf()
        }
    }

    override suspend fun getCoinsRemote(
        page: Int,
        pageSize: Int,
        vsCurrencies: String,
        sortBy: String,
        ids: List<String>
    ): List<CoinResponse> {
        Log.d(TAG, "load : net: $pageSize")
        val data = apiService.getCoinList(
            vsCurrency = vsCurrencies,
            order = sortBy,
            page = page,
            pageSize = pageSize,
            ids = ids.take(50).removeBracket()
        )
        val favouriteId: List<String> = coinDao.getAllFavourite().first().map { it.coinId }
        data.forEach {
            if (favouriteId.contains(it.id))
                it.favourite = true
        }
        return data
    }

    override suspend fun searchCoinId(query: String): List<CoinSimple> {
        val result = mutableListOf<CoinSimple>()
        val apiResult = apiService.search(query)
        apiResult.coins?.forEach {
            if (it?.thumb != null && it.id != null) {
                result.add(
                    CoinSimple(
                        id = it.id,
                        symbol = it.symbol ?: "",
                        logo = it.thumb,
                        marketCapRank = it.marketCapRank ?: 19999,
                        name = it.name ?: it.id
                    )
                )
            }
        }
        Log.d(TAG, "searchCoinId: ${result.map { it.name }}")
        return result
    }

    // update paging market data
    override suspend fun updateMarketData(coinId: String, marketData: MarketData) {
        coinDao.updateCoinPagingData(coinId, marketData)
    }

    // Coin Remote Key Repository
    override suspend fun getCoinRemoteKey(remoteKeyId: String): RemoteKey? {
        return coinRemoteKeyDao.getRemoteKey(remoteKeyId)
    }

    override suspend fun insertCoinRemoteKey(value: RemoteKey) {
        coinRemoteKeyDao.insert(value)
    }

    override suspend fun deleteCoinRemoteKey(remoteKeyId: String) {
        coinRemoteKeyDao.delete(remoteKeyId)
    }

    override suspend fun insertPair() {
        val value = apiService.getSupportedPair()
        dataStoreHelper.write(SUPPORTED_PAIR_KEY, value.toString())
    }

    override fun getCurrencyPair(): Flow<List<String>> {
        return dataStoreHelper
            .read(SUPPORTED_PAIR_KEY, String::class.java)
            .map { it.convertIntoList() }
    }
}
