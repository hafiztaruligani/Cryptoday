package com.hafiztaruligani.cryptoday.domain.usecase.favourite

import javax.inject.Inject

data class FavouriteUseCase @Inject constructor(
    val favouriteEvent: FavouriteEvent,
    val deleteFavourite: DeleteFavourite,
    val getFavouriteCoins: GetFavouriteCoins
)
