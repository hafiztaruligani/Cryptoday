package com.hafiztaruligani.cryptoday.domain.usecase.user

import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserCurrencyPair @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<String> = userRepository.getUserCurrencyPair()
}
