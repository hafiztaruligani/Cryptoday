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
import com.hafiztaruligani.cryptoday.domain.usecase.favourite.FavouriteUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import com.hafiztaruligani.cryptoday.presentation.currencies.settings.SettingUiState
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsPagedUseCase,
    private val favouriteUseCase: FavouriteUseCase,
    private val userUseCase: UserUseCase,
    getCurrenciesPairUseCase: GetCurrenciesPairUseCase
) : ViewModel() {

    private val sortBy: MutableStateFlow<SortBy> = MutableStateFlow(SortBy.MARKET_CAP_DESC())
    val query: MutableStateFlow<String> = MutableStateFlow("")
    private val userCurrencyPair = userUseCase.getUserCurrencyPair.invoke()

    private val coinsOrder = combine(
        sortBy,
        query.debounce(1000),
        userCurrencyPair
    ) { a, b, c ->
        CoinsOrder(
            sortBy = a,
            query = b,
            currencyPair = c
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = CoinsOrder())

    var coinPagingData: Flow<PagingData<Coin>> = coinsOrder
        .flatMapLatest { getCoinsUseCase.invoke(it) }
        .cachedIn(viewModelScope)

    val isQueryEmpty = query.mapLatest { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // currencies pair list and uiState, for settings dialog
    private val currenciesPair = getCurrenciesPairUseCase.invoke()
    val settingUiState = combine(currenciesPair, coinsOrder) { a, b -> SettingUiState(a, b) }

    private val _currenciesUiState = MutableStateFlow(CurrenciesUiState())
    val currenciesUiState: StateFlow<CurrenciesUiState> = _currenciesUiState

    private val isLogin: StateFlow<Boolean> = userUseCase.getUserName.invoke()
        .mapLatest { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun favouriteEvent(coin: Coin): Boolean {
        return if (!isLogin.value) {
            needLogin(true)
            false
        } else {
            favouriteUseCase.favouriteEvent.invoke(coin)
            true
        }
    }

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
        favouriteUseCase.getFavouriteCoins.invoke(it)
    }

    fun onClickButtonClear() {
        query.value = ""
    }
}
