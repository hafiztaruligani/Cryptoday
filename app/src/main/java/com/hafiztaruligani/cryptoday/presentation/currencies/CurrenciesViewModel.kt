package com.hafiztaruligani.cryptoday.presentation.currencies

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.*
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
    private val getUserCurrencyPairUseCase: GetUserCurrencyPairUseCase,
    private val setUserCurrencyPairUseCase: SetUserCurrencyPairUseCase
) : ViewModel() {
    val currenciesPair = getCurrenciesPairUseCase.invoke()

    private val sortBy: MutableStateFlow<SortBy> = MutableStateFlow(SortBy.MARKET_CAP_DESC())
    private val params: MutableStateFlow<String> = MutableStateFlow("")
    val userCurrencyPair = getUserCurrencyPairUseCase.invoke()

    val coinsOrder = combine(sortBy,params.debounce(2000),userCurrencyPair){ a, b, c ->
        Log.d(TAG, "pair: $c")
        CoinsOrder(
            ids = listOf(),
            sortBy = a,
            params = b,
            currencyPair = c
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = CoinsOrder())

    var coins : Flow<PagingData<Coin>> = coinsOrder.flatMapLatest {
        try{
            getCoins(it)
        }finally {
            _loading.value = false
            _needToScroll.value = true
        }
    }

    private val _needToScroll = MutableStateFlow(false)
    val needToScroll: StateFlow<Boolean> = _needToScroll
    fun alreadyScroll(){ _needToScroll.value = false }

    private val _loading = MutableStateFlow(false)

    val settingUiState = combine(currenciesPair, coinsOrder){ a,b -> SettingUiState(a,b) }
    val currenciesUiState = _loading.map { CurrenciesUiState(it) }

    private suspend fun getCoins(order: CoinsOrder): Flow<PagingData<Coin>> {
        if (order.params.isNotBlank())  {
            val a = searchCoinIdUseCase.invoke(order.params).filter { it is Resource.Success  }.last()

            if(a is Resource.Success) // TODO: manage error
                order.ids = a.data.map { it.id }.ifEmpty {
                    listOf( UUID.randomUUID().toString() ) //randomUUID to trigger the pager to set not found
                }
        }
        else {
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
        setUserCurrencyPairUseCase.invoke(value)
    }

    fun postSortBy(id: Int){
        if(sortBy.value.id != id)
            sortBy.value = SortBy().getSortById(id)
    }
}

