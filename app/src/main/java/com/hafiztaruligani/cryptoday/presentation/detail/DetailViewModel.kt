package com.hafiztaruligani.cryptoday.presentation.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.coin.GetCoinWithDetailUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.favourite.FavouriteUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.user.GetUserName
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCoinWithDetailUseCase: GetCoinWithDetailUseCase,
    private val getUserName: GetUserName,
    private val favouriteUseCase: FavouriteUseCase
) : ViewModel() {

    private val _detailUiState = MutableLiveData<DetailUiState>()
    val detailUiState : LiveData<DetailUiState> = _detailUiState

    val isLogin: StateFlow<Boolean> = getUserName.invoke().mapLatest { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun initData(coinId: String) = viewModelScope.launch(){
        getCoinWithDetailUseCase.invoke(coinId).collect(){
            when(it){
                is Resource.Error -> _detailUiState.postValue(DetailUiState(error = it.message))
                is Resource.Loading -> _detailUiState.postValue(DetailUiState(loading = true))
                is Resource.Success -> _detailUiState.postValue(DetailUiState(data = it.data))
            }
        }
    }

    fun favouriteEvent(coin: Coin): Boolean {
        return when{
            !isLogin.value -> false
            else -> {
                favouriteUseCase.favouriteEvent.invoke(coin)
                true
            }
        }
    }

}