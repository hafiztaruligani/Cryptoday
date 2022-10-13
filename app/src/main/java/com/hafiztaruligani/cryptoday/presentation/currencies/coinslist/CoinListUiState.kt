package com.hafiztaruligani.cryptoday.presentation.currencies.coinslist

import androidx.paging.PagingData
import com.hafiztaruligani.cryptoday.domain.model.Coin

data class CoinListUiState(
    val coins : PagingData<Coin>? = null,
    val error : String = "",
    val needToScroll : Boolean? = null
    )
