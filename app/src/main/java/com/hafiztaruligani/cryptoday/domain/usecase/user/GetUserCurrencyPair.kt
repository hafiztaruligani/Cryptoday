package com.hafiztaruligani.cryptoday.domain.usecase.user

import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import javax.inject.Inject

class GetUserCurrencyPair @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke() = userRepository.getUserCurrencyPair()
}