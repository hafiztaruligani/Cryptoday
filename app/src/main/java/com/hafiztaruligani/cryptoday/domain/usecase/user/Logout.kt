package com.hafiztaruligani.cryptoday.domain.usecase.user

import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import javax.inject.Inject

class Logout @Inject constructor(
    private val userRepository: UserRepository,
    private val coinsRepository: CoinRepository
) {
    operator fun invoke() {
        userRepository.logout()
        coinsRepository.deleteFavourites()
    }
}
