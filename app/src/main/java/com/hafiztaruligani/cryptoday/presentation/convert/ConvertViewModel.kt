package com.hafiztaruligani.cryptoday.presentation.convert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.coin.GetCoinUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.coin.SearchCoinSimpleUseCase
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ConvertViewModel @Inject constructor(
    private val searchCoinSimpleUseCase: SearchCoinSimpleUseCase,
    private val getCoinUseCase: GetCoinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConvertUiState())
    val uiState: StateFlow<ConvertUiState> = _uiState

    val coin1Param = MutableStateFlow("Bitcoin").apply {
        viewModelScope.launch {
            launch {
                collectLatest {
                    if (!_uiState.value.loading1)
                        _uiState.value = _uiState.value.copy(coin1 = null, loading1 = true)
                }
            }
            debounce(Cons.QUERY_DELAY_TIME).collectLatest { param ->
                searchCoinSimpleUseCase.invoke(param).collectLatest { resource ->
                    _uiState.value = when (resource) {
                        is Resource.Success -> ConvertUiState(
                            coins1SearchResult = resource.data,
                            coins2SearchResult = _uiState.value.coins2SearchResult,
                            error = ""
                        )
                        is Resource.Error -> ConvertUiState(error = resource.message)
                        is Resource.Loading -> _uiState.value.copy(coin1 = null, loading1 = true)
                    }
                }
            }
        }
    }

    val coin2Param = MutableStateFlow("Ethereum").apply {
        viewModelScope.launch {
            launch {
                collectLatest {
                    if (!_uiState.value.loading2)
                        _uiState.value = _uiState.value.copy(coin2 = null, loading2 = true)
                }
            }
            debounce(Cons.QUERY_DELAY_TIME).collectLatest { param ->
                searchCoinSimpleUseCase.invoke(param).collectLatest { resource ->
                    _uiState.value = when (resource) {
                        is Resource.Success -> ConvertUiState(
                            coins1SearchResult = _uiState.value.coins1SearchResult,
                            coins2SearchResult = resource.data,
                            error = ""
                        )
                        is Resource.Error -> ConvertUiState(error = resource.message)
                        is Resource.Loading -> _uiState.value.copy(coin2 = null, loading2 = true)
                    }
                }
            }
        }
    }

    private val _coin1Id = MutableStateFlow("bitcoin")
    private val _coin2Id = MutableStateFlow("ethereum")

    fun postCoinId1(coinId: String) {
        if (_coin1Id.value != coinId) _coin1Id.value = coinId
    }

    fun postCoinId2(coinId: String) {
        if (_coin2Id.value != coinId) _coin2Id.value = coinId
    }

    private val coin1: StateFlow<Coin> = _coin1Id.flatMapLatest {
        getCoinUseCase.invoke(it).filter { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(coin1 = resource.data, error = "")
                    // postCoinAmount1("1.0")
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
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Coin())

    private val coin2: StateFlow<Coin> = _coin2Id.flatMapLatest {
        getCoinUseCase.invoke(it).filter { resource ->
            when (resource) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(coin2 = resource.data, error = "")
                    // postCoinAmount1("1.0")
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
    }.stateIn(viewModelScope, SharingStarted.Eagerly, Coin())

    fun postCoinAmount1(value: String) {
        viewModelScope.launch {
            combine(coin1, coin2) { a, b ->
                Pair(a, b)
            }.collectLatest {

                val price1 = it.first?.marketData?.currentPrice ?: return@collectLatest
                val price2 = it.second?.marketData?.currentPrice ?: return@collectLatest

                val r2 = (price1 / price2) * value.toDouble()
                _uiState.value = _uiState.value.copy(
                    result = Pair(
                        value,
                        r2.toString().take(15)
                    )
                )
            }
        }
    }

    fun postCoinAmount2(value: String) {
        viewModelScope.launch {
            combine(coin1, coin2) { a, b ->
                Pair(a, b)
            }.collectLatest {

                val price1 = it.first?.marketData?.currentPrice ?: return@collectLatest
                val price2 = it.second?.marketData?.currentPrice ?: return@collectLatest

                val r1 = (price2 / price1) * value.toDouble()
                _uiState.value = _uiState.value.copy(
                    result = Pair(
                        r1.toString().take(15),
                        value
                    )
                )
            }
        }
    }

    fun swap() {
        var tmp = _coin1Id.value
        _coin1Id.value = _coin2Id.value
        _coin2Id.value = tmp

        tmp = coin1Param.value
        coin1Param.value = coin2Param.value
        coin2Param.value = tmp
    }
}
