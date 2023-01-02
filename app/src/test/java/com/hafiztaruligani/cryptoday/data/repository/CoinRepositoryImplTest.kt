package com.hafiztaruligani.cryptoday.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hafiztaruligani.cryptoday.data.local.datastore.DataStoreHelper
import com.hafiztaruligani.cryptoday.data.local.room.CoinRemoteKeyDao
import com.hafiztaruligani.cryptoday.data.local.room.FakeCoinDatabase
import com.hafiztaruligani.cryptoday.data.remote.ApiService
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder
import com.hafiztaruligani.cryptoday.util.removeBracket
import java.util.*
import kotlin.test.assertContains
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@OptIn(ExperimentalCoroutinesApi::class)
@Extensions(
    ExtendWith(MockitoExtension::class)
)
internal class CoinRepositoryImplTest {

    private lateinit var coinRepository: CoinRepository

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var remoteKeyDao: CoinRemoteKeyDao

    @Mock
    private lateinit var dataStoreHelper: DataStoreHelper

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var database: FakeCoinDatabase
    private lateinit var coinsOrder: CoinsOrder

    @BeforeEach
    fun setup() {
        database = FakeCoinDatabase()
        coinsOrder = CoinsOrder()
        coinRepository = CoinRepositoryImpl(
            apiService,
            database,
            remoteKeyDao,
            dataStoreHelper,
            userRepository,
            firebaseFirestore
        )
    }

    @Disabled
    @Test
    fun getCoinsPaged() {
    }

    @Test
    fun getCoin() {
    }

    @Test
    fun getCoinWithDetail() = runTest {
        val coinId = "coinId"

        Mockito.`when`(
            apiService.getCoinList(
                coinsOrder.currencyPair,
                coinsOrder.sortBy.apiString,
                1,
                1,
                listOf(coinId).removeBracket()
            )
        ).then { CoinResponse(id = coinId) }

        val actual = coinRepository.getCoin(coinId, coinsOrder.currencyPair)
        // assertSame(coinId, actual.coinId)
    }

    @Test
    fun insertCoins() {
    }

    @Test
    fun deleteCoins() {
    }

    @Test
    fun insertQueryHistory() = runTest {
        val data = UUID.randomUUID().toString()
        coinRepository.insertQueryHistory(data)

        assertContains(database.queryHistoryEntity.map { it.query }, data)
    }

    @Test
    fun getQueryHistory() = runTest {
        val data = coinRepository.getQueryHistory()?.first()
        assertEquals(data, database.queryHistoryEntity)
    }

    @Test
    fun getFavourite() {
    }

    @Test
    fun addFavourite() {
    }

    @Test
    fun deleteFavouriteById() {
    }

    @Test
    fun deleteFavourites() {
    }

    @Test
    fun getCoinsRemote() {
    }

    @Test
    fun searchCoinId() {
    }

    @Test
    fun updateMarketData() {
    }

    @Test
    fun getCoinRemoteKey() {
    }

    @Test
    fun insertCoinRemoteKey() {
    }

    @Test
    fun deleteCoinRemoteKey() {
    }

    @Test
    fun insertPair() {
    }

    @Test
    fun getCurrencyPair() {
    }
}
