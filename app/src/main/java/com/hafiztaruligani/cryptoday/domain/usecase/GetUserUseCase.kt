package com.hafiztaruligani.cryptoday.domain.usecase

import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke() = userRepository.getUserName()
}