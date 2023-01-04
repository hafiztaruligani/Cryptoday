package com.hafiztaruligani.cryptoday.domain.model

data class CoinSimple(
    val name: String,
    val symbol: String,
    val marketCapRank: Int,
    val id: String,
    val logo: String
) {
    fun getTitle() = "$name (${symbol.uppercase()})"
}
