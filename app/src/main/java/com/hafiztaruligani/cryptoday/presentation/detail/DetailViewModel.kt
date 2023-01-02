package com.hafiztaruligani.cryptoday.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.coin.GetCoinWithDetailUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.favourite.FavoriteEventUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.user.GetUserName
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCoinWithDetailUseCase: GetCoinWithDetailUseCase,
    private val getUserName: GetUserName,
    private val favoriteEventUseCase: FavoriteEventUseCase
) : ViewModel() {

    private val _detailUiState = MutableLiveData<DetailUiState>()
    val detailUiState: LiveData<DetailUiState> = _detailUiState

    val isLogin: StateFlow<Boolean> = getUserName.invoke().mapLatest { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun initData(coinId: String) = viewModelScope.launch() {
        getCoinWithDetailUseCase.invoke(coinId).collect() {
            when (it) {
                is Resource.Error -> _detailUiState.postValue(DetailUiState(error = it.message))
                is Resource.Loading -> _detailUiState.postValue(DetailUiState(loading = true))
                is Resource.Success -> _detailUiState.postValue(DetailUiState(data = it.data))
            }
        }
    }

    fun favouriteEvent(coin: Coin): Boolean {
        return when {
            !isLogin.value -> false
            else -> {
                favoriteEventUseCase.invoke(coin)
                true
            }
        }
    }
}
