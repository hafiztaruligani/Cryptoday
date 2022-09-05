package com.hafiztaruligani.cryptoday.presentation.currencies

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.hafiztaruligani.cryptoday.domain.usecase.GetCoinsUseCase
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrenciesViewModel @Inject constructor(private val useCase: GetCoinsUseCase) : ViewModel() {

    //private val _state = MutableLiveData<CurrenciesUiState>()
    val coins = useCase.invoke().cachedIn(viewModelScope)

    suspend fun updateCoins(){

        var i = 0
        while (true) {
            Log.d(TAG, "currenciesViewModel: called $i")
            i++
            delay(2000)
        }

    }
}