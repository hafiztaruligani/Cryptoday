package com.hafiztaruligani.cryptoday.data.remote.dto.coindetail

import com.google.gson.annotations.SerializedName
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinDetailEntity

data class CoinDetailResponse(

	@field:SerializedName("id")
	val coinId: String,

	@field:SerializedName("genesis_date")
	val genesisDate: String? = null,

	@field:SerializedName("links")
	val linksResponse: LinksResponse? = null,

	@field:SerializedName("description")
	val description: Description? = null,

	){

	fun toCoinDetailEntity():CoinDetailEntity{
		return CoinDetailEntity(
			coinId = coinId,
			genesisDate = genesisDate?.ifEmpty { "-" }?:"-",
			link = getLink(),
			description = description?.en?.ifEmpty { "-" }?:"-",
		)
	}

	private fun getLink(): String {
		return linksResponse?.homepage?.first()?:
		linksResponse?.blockchainSite?.first()?:
		linksResponse?.announcementUrl?.first()?:
		"-"
	}
}