package com.hafiztaruligani.cryptoday.presentation.main

import androidx.lifecycle.*
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.InitGlobalDataUseCase
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class MainViewModel @Inject constructor(
    private val initGlobalDataUseCase: InitGlobalDataUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    init {
        initGlobalData()
    }

    private val _uiState = MutableLiveData(MainUiState())
    val uiState: LiveData<MainUiState> = _uiState

    private val _coin = MutableStateFlow<List<Coin>>(listOf())
    val coin: StateFlow<List<Coin>?> = _coin

    private fun initGlobalData() {
        viewModelScope.launch(Dispatchers.IO) {
            initGlobalDataUseCase.invoke().collect {
                withContext(Dispatchers.Main) {
                    _uiState.value = when (it) {
                        is Resource.Success -> MainUiState(data = true)
                        is Resource.Error -> MainUiState(data = false, error = it.message)
                        is Resource.Loading -> MainUiState(data = false, loading = true)
                    }
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
