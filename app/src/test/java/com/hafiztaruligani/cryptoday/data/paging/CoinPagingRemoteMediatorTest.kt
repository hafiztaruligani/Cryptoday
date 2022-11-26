package com.hafiztaruligani.cryptoday.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinPagingDataEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.RemoteKey
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder
import com.hafiztaruligani.cryptoday.util.Cons
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@Extensions(
    ExtendWith(MockitoExtension::class)
)
internal class CoinPagingRemoteMediatorTest {

    @Mock
    private lateinit var coinRepository: CoinRepository
    private val coinsOrder = CoinsOrder()
    private lateinit var coinPagingRemoteMediator: CoinPagingRemoteMediator
    private val pageSize = Cons.PAGE_SIZE
    private val page = 10
    private lateinit var coinResponse: List<CoinResponse>
    private val remoteKeyId = CoinPagingRemoteMediator.REMOTE_KEY_ID
    private val pagingState = PagingState<Int, CoinPagingDataEntity>(
        listOf(),
        null,
        PagingConfig(
            initialLoadSize = Cons.PAGE_SIZE,
            pageSize = Cons.PAGE_SIZE,
            prefetchDistance = Cons.PRE_FETCH_DISTANCE
        ),
        10
    )

    @BeforeEach
    fun setup() {
        coinPagingRemoteMediator = CoinPagingRemoteMediator(coinRepository, coinsOrder)
        coinResponse = getCoinResponse()
    }

    fun getCoinResponse(): List<CoinResponse> {
        val data = mutableListOf<CoinResponse>()
        repeat(pageSize) {
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

    @Test
    fun `load refresh, endOfPagination= false`() = runTest {
        Mockito.`when`(
            coinRepository.getCoinsRemote(
                1, pagingState.config.initialLoadSize,
                coinsOrder.currencyPair,
                coinsOrder.sortBy.apiString,
                listOf()
            )
        ).thenReturn(coinResponse)

        val result = coinPagingRemoteMediator.load(
            LoadType.REFRESH,
            pagingState
        ) as MediatorResult.Success
        assertFalse(result.endOfPaginationReached)
    }

    @Test
    fun `load refresh by query, endOfPagination= true (data not found)`() = runTest {
        val query = "ads"
        coinsOrder.query = query
        Mockito.`when`(
            coinRepository.searchCoinId(coinsOrder.query)
        ).thenReturn(listOf())

        val result = coinPagingRemoteMediator.load(
            LoadType.REFRESH,
            pagingState
        ) as MediatorResult.Success
        assertTrue(result.endOfPaginationReached)
    }

    @Test
    fun `load refresh, error search`() = runTest {
        val query = "asd"
        coinsOrder.query = query
        Mockito.`when`(coinRepository.searchCoinId(query)).then { throw IOException() }

        val expected = MediatorResult.Error::class.simpleName
        val actual = coinPagingRemoteMediator.load(LoadType.REFRESH, pagingState)::class.simpleName
        assertSame(expected, actual)
    }

    @Test
    fun `load append, endOfPagination= false`() = runTest {
        Mockito.`when`(
            coinRepository.getCoinsRemote(
                page,
                pagingState.config.pageSize,
                coinsOrder.currencyPair,
                coinsOrder.sortBy.apiString,
                coinsOrder.ids
            )
        ).thenReturn(coinResponse)
        Mockito.`when`(coinRepository.getCoinRemoteKey(remoteKeyId))
            .thenReturn(RemoteKey(remoteKeyId, page))

        val result = coinPagingRemoteMediator.load(
            LoadType.APPEND,
            pagingState
        ) as MediatorResult.Success
        assertFalse(result.endOfPaginationReached)
    }

    @Test
    fun `load append, endOfPagination= true`() = runTest {
        Mockito.`when`(
            coinRepository.getCoinsRemote(
                page,
                pagingState.config.pageSize,
                coinsOrder.currencyPair,
                coinsOrder.sortBy.apiString,
                coinsOrder.ids
            )
        ).thenReturn(listOf())
        Mockito.`when`(coinRepository.getCoinRemoteKey(remoteKeyId))
            .thenReturn(RemoteKey(remoteKeyId, page))

        val result = coinPagingRemoteMediator.load(
            LoadType.APPEND,
            pagingState
        ) as MediatorResult.Success
        assertTrue(result.endOfPaginationReached)
    }

    @Test
    fun `load append, error`() = runTest {
        Mockito.`when`(
            coinRepository.getCoinsRemote(
                ids = listOf(),
                page = page,
                pageSize = pageSize,
                vsCurrencies = coinsOrder.currencyPair,
                sortBy = coinsOrder.sortBy.apiString,
            )
        ).then { throw IOException() }
        Mockito.`when`(
            coinRepository.getCoinRemoteKey(remoteKeyId)
        ).then { RemoteKey(remoteKeyId, page) }

        val expected = MediatorResult.Error::class.simpleName
        val actual = coinPagingRemoteMediator.load(LoadType.APPEND, pagingState)::class.simpleName
        assertSame(expected, actual)
    }
}
