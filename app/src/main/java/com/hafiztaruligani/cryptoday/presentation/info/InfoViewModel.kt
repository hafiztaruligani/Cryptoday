package com.hafiztaruligani.cryptoday.presentation.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val userUseCase: UserUseCase
) : ViewModel() {
    val description = MutableStateFlow("Cryptoday is an application that displays the latest price information for cryptocurrencies. The information is sourced from www.coingecko.com.")
    val userName: StateFlow<String> = userUseCase.getUserName.invoke()
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
}
