package com.hafiztaruligani.cryptoday.presentation.currencies.settings

import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder

data class SettingUiState(
    val currenciespair: List<String>,
    val coinsOrder: CoinsOrder
)
