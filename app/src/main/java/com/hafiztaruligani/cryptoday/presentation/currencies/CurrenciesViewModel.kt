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
import java.io.Closeable
import java.util.*
import javax.inject.Inject
import kotlin.random.Random


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsUseCase,
    private val searchCoinIdUseCase: SearchCoinIdUseCase,
    private val getCurrenciesPairUseCase: GetCurrenciesPairUseCase,
    private val getUserCurrencyPairUseCase: GetUserCurrencyPairUseCase,
    private val setUserCurrencyPairUseCase: SetUserCurrencyPairUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            while (true){
               // delay(2000)
                //a.value = a.value+1
                delay(2000)
                b.value = ('a' until 'z').random().toString()
            }
        }
    }

    val a = flow{
        repeat(1000){
            delay(2000)
            emit(it)
        }
    }//MutableStateFlow(1)
    val b = MutableStateFlow("a")
    val c = combine(a,b){a,b->Pair(a,b)}


    val currenciesPair = getCurrenciesPairUseCase.invoke()
    val _coinsOrder: MutableStateFlow<CoinsOrder> = MutableStateFlow(CoinsOrder())

    private val sortBy= MutableStateFlow(SortBy())
    val coinsOrder = combine(sortBy,getUserCurrencyPairUseCase.invoke()){a,b ->

        val oldValue = _coinsOrder.value
            CoinsOrder(
                ids = oldValue.ids,
                currencyPair = b,
                sortBy = a,
                params = oldValue.params
            )

    }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = CoinsOrder())

    var coins : Flow<PagingData<Coin>> = coinsOrder.flatMapLatest {
        getCoins(it)
    }

/*
val coins: Flow<PagingData<Coin>> = combine(getUserCurrencyPairUseCase.invoke(), coinsOrder){ a, b ->
Pair(a,b)
}.flatMapLatest {
it.second.currencyPair = it.first
getCoins(it.second)
}.cachedIn(viewModelScope)*/

    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading
    fun postLoad(value: Boolean){ _loading.value = value }

    private suspend fun getCoins(order: CoinsOrder): Flow<PagingData<Coin>> {

        if (order.params.isNotBlank()) try {
            order.ids = searchCoinIdUseCase.invoke(order.params).ifEmpty {
                listOf(
                    UUID.randomUUID().toString()
                ) //randomUUID to trigger the pager to set not found
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
            _coinsOrder.value = CoinsOrder(
                ids = _coinsOrder.value.ids,
                currencyPair = _coinsOrder.value.currencyPair,
                sortBy = _coinsOrder.value.sortBy,
                params = value
            )


        }
        loadingJob = viewModelScope.launch {
            delay(10000)
            _loading.value = false
        }
    }

    fun setUserCurrencyPair(value: String){

        setUserCurrencyPairUseCase.invoke(value)
        /*_coinsOrder.value = CoinsOrder(
                ids = _coinsOrder.value.ids,
                currencyPair = value,
                sortBy = _coinsOrder.value.sortBy,
                params = _coinsOrder.value.params
            )*/

    }

    fun postSortBy(id: Int){
        val value = SortBy().getSortById(id)
        if(_coinsOrder.value.sortBy != value) {
            _coinsOrder.value = CoinsOrder(
                ids = _coinsOrder.value.ids,
                currencyPair = _coinsOrder.value.currencyPair,
                sortBy = value,
                params = _coinsOrder.value.params
            )
            Log.d(TAG, "postSortBy: ${_coinsOrder.value}")
        }
        sortBy.value = SortBy().getSortById(id)
    }
}

