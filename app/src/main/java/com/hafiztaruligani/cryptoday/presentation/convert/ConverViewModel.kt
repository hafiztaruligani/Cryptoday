package com.hafiztaruligani.cryptoday.presentation.convert

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.domain.usecase.GetCoinUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.SearchCoinIdUseCase
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.log

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ConverViewModel @Inject constructor(
    private val searchCoinIdUseCase: SearchCoinIdUseCase,
    private val getCoinUseCase: GetCoinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConvertUiState())
    val uiState : StateFlow<ConvertUiState> = _uiState

    val coin1Param = MutableStateFlow("Bitcoin").apply {
        viewModelScope.launch {
            launch { collectLatest {
                if(!_uiState.value.loading1) _uiState.value = _uiState.value.copy(loading1 = true)
            } }
            debounce(2000).collectLatest { param ->
                searchCoinIdUseCase.invoke(param).collectLatest { resource ->
                    _uiState.value = when (resource) {
                        is Resource.Success -> ConvertUiState(coins1SearchResult = resource.data, coins2SearchResult = _uiState.value.coins2SearchResult, error = "")
                        is Resource.Error -> ConvertUiState(error = resource.message)
                        is Resource.Loading -> _uiState.value.copy(loading1 = true)
                    }
                }
            }
        }
    }

    val coin2Param = MutableStateFlow("Ethereum").apply {
        viewModelScope.launch {
            launch { collectLatest {
                if(!_uiState.value.loading2) _uiState.value = _uiState.value.copy(loading2 = true)
            } }
            debounce(2000).collectLatest { param ->
                searchCoinIdUseCase.invoke(param).collectLatest { resource ->
                    _uiState.value = when (resource) {
                        is Resource.Success ->  ConvertUiState(coins1SearchResult = _uiState.value.coins1SearchResult, coins2SearchResult = resource.data, error = "")
                        is Resource.Error -> ConvertUiState(error = resource.message)
                        is Resource.Loading -> _uiState.value.copy(loading2 = true)
                    }
                }
            }
        }
    }

    private val _coin1Id = MutableStateFlow("bitcoin")
    private val _coin2Id = MutableStateFlow("ethereum")

    private val coin1 : StateFlow<Coin?> = _coin1Id.flatMapLatest {
        getCoinUseCase.invoke(it).filter { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(coin1 = resource.data, error = "")
                    postCoinAmount1("1.0")
                    true
                }
                is Resource.Error -> {
                    _uiState.value = ConvertUiState(error = resource.message)
                    false
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(loading1 = true)
                    false
                }
            }
        }.map { resource ->
                val a = resource as Resource.Success<Coin>
                a.data
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val coin2 = _coin2Id.flatMapLatest {
        getCoinUseCase.invoke(it).filter { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(coin2 = resource.data, error = "")
                    postCoinAmount1("1.0")
                    true
                }
                is Resource.Error -> {
                    _uiState.value = ConvertUiState(error = resource.message)
                    false
                }
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(loading2 = true)
                    false
                }
            }
        }.map { resource ->
                val a = resource as Resource.Success<Coin>
                a.data
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun postCoinId1(coinId: String){
        if (_coin1Id.value != coinId) _coin1Id.value = coinId
    }

    fun postCoinId2(coinId: String){
        if (_coin2Id.value != coinId) _coin2Id.value = coinId
    }

    fun postCoinAmount1(value: String) {
        viewModelScope.launch {
            combine(coin1, coin2){a,b ->
                Pair(a,b)
            }.collectLatest {

                val price1 = it.first?.marketData?.currentPrice?:return@collectLatest//coin1.value?.marketData?.currentPrice?:0.0
                val price2 = it.second?.marketData?.currentPrice?:return@collectLatest//0.0//coin2.value?.marketData?.currentPrice?:0.0

                val r2 = (price1 / price2) * value.toDouble()
                _uiState.value = _uiState.value.copy(result = Pair(value, r2.toString()))
            }
        }
    }

    fun postCoinAmount2(value: String) {
        viewModelScope.launch {
            combine(coin1, coin2){a,b ->
                Pair(a,b)
            }.collectLatest {

                val price1 = it.first?.marketData?.currentPrice?:return@collectLatest//coin1.value?.marketData?.currentPrice?:0.0
                val price2 = it.second?.marketData?.currentPrice?:return@collectLatest//0.0//coin2.value?.marketData?.currentPrice?:0.0

                val r1 = (price2 / price1) * value.toDouble()
                _uiState.value = _uiState.value.copy(result = Pair(r1.toString(), value))
            }
        }
    }

    fun swap(){
        var tmp = _coin1Id.value
        _coin1Id.value = _coin2Id.value
        _coin2Id.value = tmp

        tmp = coin1Param.value
        coin1Param.value = coin2Param.value
        coin2Param.value = tmp
    }

    var job: Job?=null
    fun a(){

    }

}