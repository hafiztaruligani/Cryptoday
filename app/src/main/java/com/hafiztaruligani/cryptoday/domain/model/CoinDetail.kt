package com.hafiztaruligani.cryptoday.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey



data class CoinDetail(
    val genesisDate: String,
    val link: String,
    val description: String,
)
