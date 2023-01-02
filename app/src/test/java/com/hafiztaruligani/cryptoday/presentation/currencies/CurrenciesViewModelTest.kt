package com.hafiztaruligani.cryptoday.presentation.currencies

import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import com.hafiztaruligani.cryptoday.domain.usecase.coin.GetCoinsPagedUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.coin.GetCurrenciesPairUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.coin.SortBy
import com.hafiztaruligani.cryptoday.domain.usecase.favourite.FavoriteEventUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.favourite.GetFavoriteCoinsUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.user.GetUserCurrencyPair
import com.hafiztaruligani.cryptoday.domain.usecase.user.GetUserName
import com.hafiztaruligani.cryptoday.domain.usecase.user.Logout
import com.hafiztaruligani.cryptoday.domain.usecase.user.SetUserCurrencyPair
import com.hafiztaruligani.cryptoday.domain.usecase.user.SetUserName
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import com.hafiztaruligani.cryptoday.util.Cons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
internal class CurrenciesViewModelTest {

    @Mock
    private lateinit var getCoinsPagedUseCase: GetCoinsPagedUseCase
    @Mock
    private lateinit var favoriteEventUseCase: FavoriteEventUseCase
    @Mock
    private lateinit var getFavoriteCoinsUseCase: GetFavoriteCoinsUseCase

    @Mock
    private lateinit var getUserCurrencyPair: GetUserCurrencyPair
    @Mock
    private lateinit var getUserName: GetUserName
    @Mock
    private lateinit var setUserName: SetUserName
    @Mock
    private lateinit var setUserCurrencyPair: SetUserCurrencyPair
    @Mock
    private lateinit var logout: Logout
    private lateinit var userUseCase: UserUseCase

    @Mock
    private lateinit var getCurrenciesPairUseCase: GetCurrenciesPairUseCase
    private lateinit var viewModel: CurrenciesViewModel
    private val dispatcher = UnconfinedTestDispatcher()

    private val currencyPair = listOf("usd", "idr")

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        Cons.QUERY_DELAY_TIME = 0
        userUseCase = UserUseCase(
            getUserCurrencyPair,
            getUserName,
            setUserName,
            setUserCurrencyPair,
            logout
        )

        setupMock()

        viewModel = CurrenciesViewModel(
            getCoinsPagedUseCase,
            userUseCase,
            getFavoriteCoinsUseCase,
            favoriteEventUseCase,
            getCurrenciesPairUseCase
        )
    }

    private fun setupMock() {
        Mockito.`when`(userUseCase.getUserCurrencyPair.invoke()).thenReturn(
            flow { emit("USD") }
        )
        Mockito.`when`(getCurrenciesPairUseCase.invoke()).thenReturn(
            flow {
                emit(currencyPair)
            }
        )
    }

    @Test
    fun getQuery() {
        val string = "asd"
        viewModel.query.value = string
        assertEquals(
            string,
            viewModel.query.value
        )
    }

    @Test
    fun isQueryEmpty_true() {
        viewModel.query.value = ""
        assertTrue(
            viewModel.isQueryEmpty.value
        )
    }

    @Test
    fun isQueryEmpty_false() {
        viewModel.query.value = "asd"
        assertFalse(
            viewModel.isQueryEmpty.value
        )
    }

    @Test
    fun getSettingUiState() = runTest {
        val sortById = SortBy.MARKEY_CAP_ASC().id
        viewModel.postSortBy(sortById)

        val result = viewModel.settingUiState.first()
        val actual = Pair(result.coinsOrder.sortBy.id, result.currenciespair)
        val expected = Pair(sortById, currencyPair)
        assertEquals(expected, actual)
    }

    @Test
    fun favouriteEvent_hasLogin() {
        Mockito.`when`(userUseCase.getUserName.invoke()).thenReturn(flow { emit("asd") })
        val coin = getCoin()
        val isSuccess = viewModel.favouriteEvent(coin)
        assertTrue(isSuccess)
    }

    @Test
    fun favouriteEvent_notLogin() {
        Mockito.`when`(userUseCase.getUserName.invoke()).thenReturn(flow { emit("") })
        val coin = getCoin()
        val isSuccess = viewModel.favouriteEvent(coin)
        assertFalse(isSuccess)
    }

    @Test
    fun postSortBy() = runBlocking {
        val expected = SortBy.MARKEY_CAP_ASC().id
        viewModel.postSortBy(expected)

        val actual = viewModel.settingUiState.first().coinsOrder.sortBy.id
        assertEquals(expected, actual)
    }

    @Test
    fun needLogin() {
        val expected = true
        viewModel.needLogin(expected)
        assertTrue(
            viewModel.currenciesUiState.value.needLogin
        )
    }

    @Test
    fun getFavouriteCoins() = runTest {
        Mockito.`when`(
            getFavoriteCoinsUseCase.invoke(
                viewModel.settingUiState.first().coinsOrder
            )
        ).thenReturn(
            flow { emit(listOf(getCoin())) }
        )

        val result = viewModel.favouriteCoins.first()
        assertTrue(
            result.first().isFavorite
        )
    }

    @Test
    fun onClickButtonClear() {
        viewModel.query.value = "asd"
        viewModel.onClickButtonClear()
        assertTrue(viewModel.query.value.isBlank())
    }

    private fun getCoin(): Coin = Coin(
        id = "asd",
        symbol = "asd",
        name = "asd",
        image = "asd",
        marketData = MarketData(),
        isFavorite = true
    )
}
