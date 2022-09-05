package com.hafiztaruligani.cryptoday.presentation.detail

import com.hafiztaruligani.cryptoday.domain.model.CoinWithDetail

data class DetailUiState(
    val data : CoinWithDetail?=null,
    val error : String = "",
    val loading : Boolean = false
)
