package com.hafiztaruligani.cryptoday.presentation.currencies

import androidx.paging.PagingData
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.util.Resource

data class CurrenciesUiState(
    val loading : Boolean = false,
    val data : PagingData<Coin>? = null,
    val error : String = ""
    )
