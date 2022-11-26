package com.hafiztaruligani.cryptoday.data.remote.dto.search

import com.google.gson.annotations.SerializedName

data class CoinsItem(
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("market_cap_rank")
    val marketCapRank: Int? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("large")
    val thumb: String? = null
)
