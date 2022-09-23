package com.hafiztaruligani.cryptoday.presentation.currencies.coinslist

import androidx.paging.PagingData
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.usecase.CoinsOrder
import com.hafiztaruligani.cryptoday.util.Resource

data class CoinListUiState(
    val coins : PagingData<Coin>? = null,
    val error : String = "",
    val needToScroll : Boolean? = null
    )
