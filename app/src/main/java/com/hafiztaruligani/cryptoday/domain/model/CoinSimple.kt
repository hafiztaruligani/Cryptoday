package com.hafiztaruligani.cryptoday.domain.model

import com.google.gson.annotations.SerializedName

data class CoinSimple(
    val name: String,
    val marketCapRank: Int,
    val id: String,
    val logo: String
)