package com.hafiztaruligani.cryptoday.domain.usecase.coin

import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetCurrenciesPairUseCase @Inject constructor(private val coinRepository: CoinRepository) {
    operator fun invoke(): Flow<List<String>> = coinRepository.getCurrencyPair()
}
