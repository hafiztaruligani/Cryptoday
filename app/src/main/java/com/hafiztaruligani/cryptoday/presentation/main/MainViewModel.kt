package com.hafiztaruligani.cryptoday.presentation.main

import android.util.Log
import androidx.lifecycle.*
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.GetCoinsPagedUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.InitGlobalDataUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsPagedUseCase,
    private val initGlobalDataUseCase: InitGlobalDataUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {
    init {
        initGlobalData()
    }

    private val _uiState = MutableLiveData<MainUiState>()
    val uiState : LiveData<MainUiState> = _uiState

    private val _coin= MutableStateFlow<List<Coin>>(listOf())
    val coin : StateFlow<List<Coin>?> = _coin

    private fun initGlobalData(){
        viewModelScope.launch (Dispatchers.IO) {
            initGlobalDataUseCase.invoke().collect() {
                when (it) {
                    is Resource.Success -> _uiState.postValue(MainUiState(data = true))
                    is Resource.Error -> _uiState.postValue(MainUiState(data = false, error = it.message))
                    is Resource.Loading -> _uiState.postValue(
                        MainUiState(
                            data = false,
                            loading = true
                        )
                    )
                }
            }
        }
    }
    fun getUserName() = userUseCase.getUserName.invoke()
    fun setUserName(value: String) = viewModelScope.launch {
        userUseCase.setUserName.invoke(value)
    }

    fun logout() {
        userUseCase.logout.invoke()
    }
}