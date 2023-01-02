package com.hafiztaruligani.cryptoday.presentation.convert

import com.hafiztaruligani.cryptoday.Util
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import com.hafiztaruligani.cryptoday.domain.usecase.coin.GetCoinUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.coin.SearchCoinSimpleUseCase
import com.hafiztaruligani.cryptoday.errorNull
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.Resource
import java.util.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
internal class ConvertViewModelTest {

    @Mock
    private lateinit var searchCoinSimpleUseCase: SearchCoinSimpleUseCase
    @Mock
    private lateinit var getCoinUseCase: GetCoinUseCase

    private lateinit var viewModel: ConvertViewModel

    private val util = Util()
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Cons.QUERY_DELAY_TIME = 0
        Dispatchers.setMain(dispatcher)
        setupMock()
        viewModel = ConvertViewModel(searchCoinSimpleUseCase, getCoinUseCase)
    }

    private fun setupMock() = runTest {
        mockSearchCoinSimple()
        mockGetCoin()
    }

    private suspend fun mockGetCoin(
        coinId: String? = null,
        coin: Coin? = null,
        setError: Boolean = false
    ) {
        Mockito.`when`(getCoinUseCase.invoke(coinId ?: Mockito.anyString()))
            .thenReturn(
                flow {
                    repeat(4) {
                        emit(
                            if (setError) Resource.Error()
                            else Resource.Success(coin ?: getCoin())
                        )
                    }
                }
            )
    }

    private suspend fun mockSearchCoinSimple(
        param: String? = null,
        coinSimpleList: List<CoinSimple>? = null,
        setError: Boolean = false
    ) {
        Mockito.`when`(
            searchCoinSimpleUseCase.invoke(
                param ?: Mockito.anyString()
            )
        ).thenReturn(
            flowOf(
                if (setError) Resource.Error()
                else Resource.Success(coinSimpleList ?: listOf())
            )
        )
    }

    @Disabled
    @Test
    fun getUiState() {
    }

    @Test
    fun `SUCCESS set Coin1 Param, uiState coin1SearchResult notEmpty`() = runTest {
        val param = util.getRandomString(5)
        val coinSimpleList = getCoinSimpleList()
        mockSearchCoinSimple(param, coinSimpleList)

        viewModel.coin1Param.value = param
        val result = viewModel.uiState.value.coins1SearchResult
        assertTrue(
            result?.isNotEmpty() == true
        )
        assertEquals(
            coinSimpleList,
            result
        )
    }

    @Test
    fun `ERROR set Coin1 Param, uiState error not blank and coin1searchResult null`() = runTest {
        val param = util.getRandomString(5)
        mockSearchCoinSimple(param, setError = true)

        viewModel.coin1Param.value = param
        assertTrue(
            viewModel.uiState.value.error.isNotBlank()
        )

        assertNull(
            viewModel.uiState.value.coins1SearchResult
        )
    }

    @Test
    fun `SUCCESS set Coin2 Param, uiState coin2SearchResult notEmpty`() = runTest {
        val param = util.getRandomString(5)
        val coinSimpleList = getCoinSimpleList()
        mockSearchCoinSimple(param, coinSimpleList)

        viewModel.coin2Param.value = param
        val result = viewModel.uiState.value.coins2SearchResult
        assertTrue(
            result?.isNotEmpty() == true
        )
        assertEquals(
            coinSimpleList,
            result
        )
    }

    @Test
    fun `ERROR set Coin2 Param, uiState error not blank and coin2SearchResult null`() = runTest {
        val param = util.getRandomString(5)
        mockSearchCoinSimple(param, setError = true)

        viewModel.coin2Param.value = param
        assertTrue(
            viewModel.uiState.value.error.isNotBlank()
        )

        assertNull(
            viewModel.uiState.value.coins2SearchResult
        )
    }

    @Test
    fun postCoinId1() = runTest {
        val id = util.getRandomString(5)
        mockGetCoin(id, getCoin(id))

        viewModel.postCoinId1(id)
        val result = viewModel.uiState.value.coin1?.id
        assertEquals(
            id,
            result
        )
    }

    @Test
    fun postCoinId2() = runTest {
        val id = util.getRandomString(5)
        mockGetCoin(id, getCoin(id))

        viewModel.postCoinId2(id)
        val result = viewModel.uiState.value.coin2?.id
        assertEquals(
            id,
            result
        )
    }

    @Test
    fun postCoinAmount1() {
        val uiState = viewModel.uiState.value
        val price1 = uiState.coin1?.marketData?.currentPrice ?: errorNull()
        val price2 = uiState.coin2?.marketData?.currentPrice ?: errorNull()
        println("PRICE $price1 $price2")
    }

    @Disabled
    @Test
    fun postCoinAmount2() {
    }

    @Test
    fun swap() = runBlocking {
        val uiState = viewModel.uiState.value

        val c1Before = viewModel.uiState.value.coin1?.id ?: errorNull()
        val c2Before = viewModel.uiState.value.coin2?.id ?: errorNull()
        viewModel.swap()

        val c1After = viewModel.uiState.value.coin1?.id ?: errorNull()
        val c2After = viewModel.uiState.value.coin2?.id ?: errorNull()

        println("COIN")
        println(c1Before)
        println(c1After)
        println(c2Before)
        println(c2After)
        assertEquals(c1After, c2Before)
        assertEquals(c2After, c1Before)
    }

    private fun getCoinSimpleList(): MutableList<CoinSimple> {
        val result = mutableListOf<CoinSimple>().apply {
            repeat(5) {
                add(getCoinSimple(it))
            }
        }
        return result
    }

    private fun getCoinSimple(it: Int) = CoinSimple(
        it.toString(),
        it,
        util.getRandomString(5),
        it.toString()
    )

    private fun getCoin(coinId: String? = null) = Coin(
        coinId ?: util.getRandomString(5),
        "",
        "",
        "",
        MarketData(currentPrice = Random().nextDouble()),
        true
    )
}
