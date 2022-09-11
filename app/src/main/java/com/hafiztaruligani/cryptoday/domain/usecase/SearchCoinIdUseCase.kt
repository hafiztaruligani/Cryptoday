package com.hafiztaruligani.cryptoday.domain.usecase

import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import javax.inject.Inject


class SearchCoinIdUseCase @Inject constructor(
    val coinRepository: CoinRepository
) {
    suspend operator fun invoke(params: String): List<String> = coinRepository.searchCoinId(params)
}