package com.hafiztaruligani.cryptoday.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hafiztaruligani.cryptoday.domain.model.CoinWithDetail
import com.hafiztaruligani.cryptoday.domain.usecase.GetCoinWithDetailUseCase
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val getCoinWithDetailUseCase: GetCoinWithDetailUseCase) : ViewModel() {

    private val _detailUiState = MutableLiveData<DetailUiState>()
    val detailUiState : LiveData<DetailUiState> = _detailUiState

    fun initData(coinId: String) = viewModelScope.launch(){
        getCoinWithDetailUseCase.invoke(coinId).collect(){
            when(it){
                is Resource.Error -> _detailUiState.postValue(DetailUiState(error = it.message))
                is Resource.Loading -> _detailUiState.postValue(DetailUiState(loading = true))
                is Resource.Success -> _detailUiState.postValue(DetailUiState(data = it.data))
            }
        }
    }

}