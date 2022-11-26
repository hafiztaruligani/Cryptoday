package com.hafiztaruligani.cryptoday.domain.usecase.favourite

import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteFavourite @Inject constructor(private val coinRepository: CoinRepository) {
    operator fun invoke(coinId: String) = CoroutineScope(Dispatchers.IO).launch {
        coinRepository.deleteFavouriteById(coinId)
    }
}
