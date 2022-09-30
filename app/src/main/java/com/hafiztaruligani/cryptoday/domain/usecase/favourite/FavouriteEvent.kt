package com.hafiztaruligani.cryptoday.domain.usecase.favourite

import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavouriteEvent @Inject constructor(private val coinRepository: CoinRepository) {
    operator fun invoke(coin: Coin) = CoroutineScope(Dispatchers.IO).launch {
        if(coin.favourite) coinRepository.addFavourite(coin)
        else coinRepository.deleteFavouriteById(coin.id)
    }
}