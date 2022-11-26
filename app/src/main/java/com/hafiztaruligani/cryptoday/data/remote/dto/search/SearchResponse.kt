package com.hafiztaruligani.cryptoday.data.remote.dto.search

import com.google.gson.annotations.SerializedName

data class SearchResponse(

    @field:SerializedName("coins")
    val coins: List<CoinsItem?>? = null
)
