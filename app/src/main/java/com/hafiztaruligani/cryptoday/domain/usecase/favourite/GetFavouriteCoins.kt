package com.hafiztaruligani.cryptoday.domain.usecase.favourite

import com.hafiztaruligani.cryptoday.data.local.room.AppDatabase
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder
import com.hafiztaruligani.cryptoday.domain.usecase.coin.SortBy
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetFavouriteCoins @Inject constructor(
    private val coinRepository: CoinRepository,
    private val appDatabase: AppDatabase
) {
    operator fun invoke(coinsOrder: CoinsOrder): Flow<List<Coin>> {
        return coinRepository.getFavourite(coinsOrder).map { list ->
            (
                    if (coinsOrder.sortBy is SortBy.MARKET_CAP_DESC)
                        list.sortedBy { it.rank }
                    else
                        list.sortedByDescending { it.rank }
                    )
                .map { it.toCoin() }
        }
    }

}