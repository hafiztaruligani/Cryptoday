package com.hafiztaruligani.cryptoday.presentation.currencies

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.*
import com.hafiztaruligani.cryptoday.domain.usecase.favourite.FavouriteUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import com.hafiztaruligani.cryptoday.presentation.currencies.settings.SettingUiState
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsPagedUseCase,
    private val searchCoinIdUseCase: SearchCoinIdUseCase,
    private val getCurrenciesPairUseCase: GetCurrenciesPairUseCase,
    private val favouriteUseCase: FavouriteUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    val currenciesPair = getCurrenciesPairUseCase.invoke()

    private val sortBy: MutableStateFlow<SortBy> = MutableStateFlow(SortBy.MARKET_CAP_DESC())
    private val params: MutableStateFlow<String> = MutableStateFlow("")
    private val userCurrencyPair = userUseCase.getUserCurrencyPair.invoke()
    val isLogin: StateFlow<Boolean> = userUseCase.getUserName.invoke().mapLatest { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val coinsOrder = combine(sortBy,params.debounce(2000),userCurrencyPair){ a, b, c ->
        CoinsOrder(
            ids = listOf(),
            sortBy = a,
            params = b,
            currencyPair = c
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = CoinsOrder())

    var coins : Flow<PagingData<Coin>> = coinsOrder.flatMapLatest {
       // try{
            getCoins(it)
        /*}finally {
            _loading.value = false
            _needToScroll.value = true
        }*/
    }.cachedIn(viewModelScope)

    private val _needToScroll = MutableStateFlow(false)
    val needToScroll: StateFlow<Boolean> = _needToScroll
    fun alreadyScroll(){ _needToScroll.value = false }

    private val _loading = MutableStateFlow(false)

    val settingUiState = combine(currenciesPair, coinsOrder){ a,b -> SettingUiState(a,b) }

    private val _currenciesUiState = MutableStateFlow(CurrenciesUiState())
    val currenciesUiState = merge(_loading.map { CurrenciesUiState(loading = it) }, _currenciesUiState)

    private suspend fun getCoins(order: CoinsOrder): Flow<PagingData<Coin>> {
        if (order.params.isNotBlank()) {
            val a = searchCoinIdUseCase.invoke(order.params).filter { it is Resource.Success  }.last()

            if(a is Resource.Success) // TODO: manage error
                order.ids = a.data.map { it.id }.ifEmpty {
                    listOf( UUID.randomUUID().toString() ) //randomUUID to trigger the pager to set not found
                }
        } else {
            order.ids = listOf()
        }
        return getCoinsUseCase.invoke(order).cachedIn(viewModelScope)
    }

    fun postParams(value: String) {
        if(params.value != value) {
            params.value = value
            _loading.value = true
        }
    }

    fun postUserCurrencyPair(value: String){
        userUseCase.setUserCurrencyPair.invoke(value)
    }

    fun postSortBy(id: Int){
        if(sortBy.value.id != id)
            sortBy.value = SortBy().getSortById(id)
    }

    fun favouriteEvent(coin: Coin): Boolean {
        return when{
            !isLogin.value -> {
                Log.d(TAG, "favouriteEvent: need login")
                needLogin(true)
                false
            }
            else -> {
                favouriteUseCase.favouriteEvent.invoke(coin)
                true
            }
        }
    }

    fun needLogin(value: Boolean) {
        _currenciesUiState.value = CurrenciesUiState(needLogin = value)
    }

    val favouriteCoins = coinsOrder.flatMapLatest {
        favouriteUseCase.getFavouriteCoins.invoke(it)
    }

}

