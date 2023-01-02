package com.hafiztaruligani.cryptoday.domain.usecase.favourite

import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder
import com.hafiztaruligani.cryptoday.domain.usecase.coin.SortBy
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoriteCoinsUseCase @Inject constructor(
    private val coinRepository: CoinRepository
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
