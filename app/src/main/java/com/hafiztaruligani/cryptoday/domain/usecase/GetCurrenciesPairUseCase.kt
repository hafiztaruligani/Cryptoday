package com.hafiztaruligani.cryptoday.domain.usecase

import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrenciesPairUseCase @Inject constructor(private val coinRepository: CoinRepository) {
    operator fun invoke(): Flow<List<String>> = coinRepository.getCurrencyPair()
}