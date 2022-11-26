package com.hafiztaruligani.cryptoday.domain.usecase.coin

import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinPagingDataEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.QueryHistoryEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.RemoteKey
import com.hafiztaruligani.cryptoday.data.paging.CoinPagingRemoteMediator.Companion.REMOTE_KEY_ID
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Cons
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
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
internal class GetCoinsPagedUseCaseTest {

    @Mock
    private lateinit var coinRepository: CoinRepository
    private val coinsOrder = CoinsOrder()

    private lateinit var coinPagingDataEntity: MutableList<CoinPagingDataEntity>
    private var remoteKeyEntity: RemoteKey? = null
    private lateinit var coinResponse: List<CoinResponse>

    @BeforeEach
    fun setup() = runTest {
        Cons.UPDATE_DELAY_TIME = 1
        coinPagingDataEntity = getCoinPagingDataEntity()
        coinResponse = getCoinResponse()

        Mockito.lenient().`when`(coinRepository.deleteCoins()).then { deleteAllData() }
        Mockito.lenient().`when`(coinRepository.deleteCoinRemoteKey(REMOTE_KEY_ID)).then {
            deleteRemoteKey()
        }
    }

    private fun getCoinResponse(): List<CoinResponse> {
        val data = mutableListOf<CoinResponse>()
        repeat(Cons.PAGE_SIZE) {
            data.add(
                CoinResponse(
                    id = "$it",
                    symbol = "1",
                    name = "1",
                    image = "1",
                    favourite = false
                )
            )
        }
        return data.toList()
    }

    private fun getCoinPagingDataEntity(): MutableList<CoinPagingDataEntity> {
        val data = mutableListOf<CoinPagingDataEntity>()
        repeat(Cons.PAGE_SIZE) {
            data.add(
                CoinPagingDataEntity(
                    coinId = it.toString(),
                    symbol = it.toString(),
                    name = it.toString(),
                    marketData = getMarketData(it),
                    rank = it,
                    isFavorite = false,
                    image = ""
                )
            )
        }
        return data
    }

    private fun getMarketData(it: Int) = MarketData(
        it.toString(),
        it,
        it.toDouble(),
        it.toDouble(),
        it.toDouble(),
        it.toDouble(),
        it.toDouble(),
        it.toDouble(),
        it.toDouble(),
        it.toDouble(),
        it.toString(),
        it.toString()
    )

    private fun updateData(data: CoinResponse) {
        val coinUpdate = data.toCoinEntity(currencyPair = coinsOrder.currencyPair)
        val index = coinPagingDataEntity.map { it.coinId }.indexOf(coinUpdate.coinId)
        coinPagingDataEntity[index] = coinUpdate
    }

    private fun deleteAllData() { coinPagingDataEntity = mutableListOf() }
    private fun deleteRemoteKey() { remoteKeyEntity = null }

    @Test
    fun `last search history EMPTY, old data should NOT be deleted`() = runTest {
        Mockito.`when`(coinRepository.getQueryHistory()).thenReturn(
            flow {
                emit(
                    listOf(QueryHistoryEntity("", 1))
                )
            }
        )

        GetCoinsPagedUseCase(coinRepository)
        assertTrue(coinPagingDataEntity.isNotEmpty())
    }

    @Test
    fun `last search history NOT_EMPTY, old data should be deleted`() = runTest {
        Mockito.`when`(coinRepository.getQueryHistory()).thenReturn(
            flow {
                emit(
                    listOf(QueryHistoryEntity("asd", 1))
                )
            }
        )

        GetCoinsPagedUseCase(coinRepository)

        assertTrue(coinPagingDataEntity.isEmpty())
        assertNull(remoteKeyEntity)
    }

    @Test
    fun `check data is updated`() = runBlocking {

        coinPagingDataEntity = getCoinPagingDataEntity()
        val oldData = getCoinPagingDataEntity()
        val page = 1

        // repo get remote key
        Mockito.`when`(coinRepository.getCoinRemoteKey(REMOTE_KEY_ID))
            .thenReturn(RemoteKey(REMOTE_KEY_ID, page))

        // repo get coins remote
        Mockito.`when`(
            coinRepository.getCoinsRemote(
                page,
                Cons.PAGE_SIZE,
                coinsOrder.currencyPair,
                coinsOrder.sortBy.apiString,
                coinsOrder.ids
            )
        ).thenReturn(coinResponse)

        // repo insert/update data
        coinResponse.forEach { data ->
            Mockito.`when`(
                coinRepository.updateMarketData(
                    data.id!!,
                    data.getMarketData(coinsOrder.currencyPair)
                )
            ).then { updateData(data) }
        }

        GetCoinsPagedUseCase(coinRepository).invoke(coinsOrder)
        delay(Cons.UPDATE_DELAY_TIME + 100)
        assertNotEquals(coinPagingDataEntity, oldData)
    }

    @Test
    fun `assert history has inserted or called`() = runTest {
        var historyHasInserted = false
        val query = "asd"
        Mockito.`when`(coinRepository.insertQueryHistory(query)).then {
            historyHasInserted = true
            Unit
        }

        coinsOrder.query = query
        GetCoinsPagedUseCase(coinRepository).invoke(coinsOrder)
        assertTrue(historyHasInserted)
    }
}
