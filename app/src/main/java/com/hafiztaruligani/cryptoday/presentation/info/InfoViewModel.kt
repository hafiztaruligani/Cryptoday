package com.hafiztaruligani.cryptoday.presentation.info

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hafiztaruligani.cryptoday.domain.usecase.user.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class InfoViewModel @Inject constructor(
    userUseCase: UserUseCase
) : ViewModel() {

    private val description = MutableStateFlow("Cryptoday is an application that displays the latest price information for cryptocurrencies. The information is sourced from www.coingecko.com.")
    private val userName = userUseCase.getUserName.invoke()

    val uiState = combine(description, userName){ description, username ->
        InfoUIState(description, username)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        InfoUIState("", "")
    )

}

data class InfoUIState(
    val description: String,
    val userName: String
)
