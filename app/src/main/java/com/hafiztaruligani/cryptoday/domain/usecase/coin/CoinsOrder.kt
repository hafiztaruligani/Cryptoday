package com.hafiztaruligani.cryptoday.domain.usecase.coin

import java.io.IOException

data class CoinsOrder(
    var ids: List<String> = listOf(),
    var currencyPair: String = "",
    var sortBy: SortBy = SortBy.MARKET_CAP_DESC(),
    var query: String = ""
)

open class SortBy(val id: Int, val apiString: String, val presentationString: String) {
    constructor() : this(id = 0, apiString = "", presentationString = "")

    class MARKET_CAP_DESC : SortBy(1, "market_cap_desc", "Market Cap Desc")
    class MARKEY_CAP_ASC : SortBy(2, "market_cap_asc", "Market Cap Asc")

    fun getSortById(id: Int): SortBy {
        return when (id) {
            1 -> MARKET_CAP_DESC()
            2 -> MARKEY_CAP_ASC()
            else -> {
                throw IOException("'$id' sort id not found")
            }
        }
    }

    fun getAllType(): List<SortBy> {
        return listOf(
            MARKET_CAP_DESC(),
            MARKEY_CAP_ASC()
        )
    }
}
