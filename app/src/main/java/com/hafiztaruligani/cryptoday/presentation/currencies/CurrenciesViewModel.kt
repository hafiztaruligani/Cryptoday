package com.hafiztaruligani.cryptoday.presentation.currencies

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.*
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
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

    val coinsOrder = combine(sortBy,params,userCurrencyPair){a,b,c ->
        Log.d(TAG, "pair: $c")
        CoinsOrder(
            ids = listOf(),
            sortBy = a,
            params = b,
            currencyPair = c
        )
    }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = CoinsOrder())

    var coins : Flow<PagingData<Coin>> = coinsOrder.flatMapLatest {
        getCoins(it)
    }

    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading
    fun postLoad(value: Boolean){ _loading.value = value }

    private suspend fun getCoins(order: CoinsOrder): Flow<PagingData<Coin>> {

        if (order.params.isNotBlank()) try {
            order.ids = searchCoinIdUseCase.invoke(order.params).ifEmpty {
                listOf( UUID.randomUUID().toString() ) //randomUUID to trigger the pager to set not found
            }
        } catch (e: Exception) {
        }
        else {
            order.ids = listOf()
        }
        _loading.value = false
        Log.d(TAG, "coinsorder: $order")
        return getCoinsUseCase.invoke(order).cachedIn(viewModelScope)

    }

    var job: Job? = null
    var loadingJob: Job?=null
    fun postParams(value: String){
        job?.cancel()
        loadingJob?.cancel()
        job = viewModelScope.launch {
            if (value.isNotBlank()) {
                _loading.value = true
                delay(2000)
            }
            yield()
            params.value = value
        }
        loadingJob = viewModelScope.launch {
            delay(10000)
            _loading.value = false
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

