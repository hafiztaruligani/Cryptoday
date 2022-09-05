package com.hafiztaruligani.cryptoday.presentation.main

import android.util.Log
import androidx.lifecycle.*
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.GetCoinsUseCase
import com.hafiztaruligani.cryptoday.util.Cons
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getCoinsUseCase: GetCoinsUseCase): ViewModel() {
    init {
        Log.d(Cons.TAG, "mainViewModel: called")
    }
    private val _coin= MutableStateFlow<List<Coin>>(listOf())
    val coin : StateFlow<List<Coin>?> = _coin

}