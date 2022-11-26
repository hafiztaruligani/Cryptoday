package com.hafiztaruligani.cryptoday.presentation.convert

import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.model.CoinSimple

data class ConvertUiState(
    val loading1: Boolean = false,
    val loading2: Boolean = false,
    val coin1: Coin? = null,
    val coin2: Coin? = null,
    val coins1SearchResult: List<CoinSimple>? = null,
    val coins2SearchResult: List<CoinSimple>? = null,
    val result: Pair<String, String>? = null,
    val error: String = ""
)
