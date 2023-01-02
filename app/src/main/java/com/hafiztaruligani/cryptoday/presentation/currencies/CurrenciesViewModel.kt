package com.hafiztaruligani.cryptoday.presentation.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder
import com.hafiztaruligani.cryptoday.domain.usecase.coin.GetCoinsPagedUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.coin.GetCurrenciesPairUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.coin.SortBy
import com.hafiztaruligani.cryptoday.domain.usecase.favourite.FavoriteEventUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.favourite.GetFavoriteCoinsUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import com.hafiztaruligani.cryptoday.presentation.currencies.settings.SettingUiState
import com.hafiztaruligani.cryptoday.util.Cons
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsPagedUseCase,
    private val userUseCase: UserUseCase,
    private val getFavoriteCoinsUseCase: GetFavoriteCoinsUseCase,
    private val favoriteEventUseCase: FavoriteEventUseCase,
    getCurrenciesPairUseCase: GetCurrenciesPairUseCase
) : ViewModel() {

    private val sortBy: MutableStateFlow<SortBy> = MutableStateFlow(SortBy.MARKET_CAP_DESC())
    val query: MutableStateFlow<String> = MutableStateFlow("")
    private val userCurrencyPair = userUseCase.getUserCurrencyPair.invoke()

    private val coinsOrder = combine(
        sortBy,
        query.debounce(Cons.QUERY_DELAY_TIME),
        userCurrencyPair
    ) { a, b, c ->
        CoinsOrder(
            sortBy = a,
            query = b,
            currencyPair = c
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = CoinsOrder())

    val coinPagingData: Flow<PagingData<Coin>> = coinsOrder
        .flatMapLatest { getCoinsUseCase.invoke(it) }
        .cachedIn(viewModelScope)

    val isQueryEmpty = query.mapLatest { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // currencies pair list and uiState, for settings dialog
    private val currenciesPair = getCurrenciesPairUseCase.invoke()
    val settingUiState = combine(currenciesPair, coinsOrder) { a, b -> SettingUiState(a, b) }

    private val _currenciesUiState = MutableStateFlow(CurrenciesUiState())
    val currenciesUiState: StateFlow<CurrenciesUiState> = _currenciesUiState

    // return isSuccess, depend on is user login
    fun favouriteEvent(coin: Coin): Boolean {
        val login: Boolean = runBlocking { isLogin() }
        return if (!login) {
            needLogin(true)
            false
        } else {
            favoriteEventUseCase.invoke(coin)
            true
        }
    }

    private suspend fun isLogin() = userUseCase
        .getUserName.invoke().firstOrNull()?.isNotEmpty()
        ?: false

    fun postUserCurrencyPair(value: String) {
        userUseCase.setUserCurrencyPair.invoke(value)
    }

    fun postSortBy(id: Int) {
        if (sortBy.value.id != id)
            sortBy.value = SortBy().getSortById(id)
    }

    fun needLogin(value: Boolean) {
        _currenciesUiState.value = CurrenciesUiState(needLogin = value)
    }

    val favouriteCoins = coinsOrder.flatMapLatest {
        getFavoriteCoinsUseCase.invoke(it)
    }

    fun onClickButtonClear() {
        query.value = ""
    }
}
