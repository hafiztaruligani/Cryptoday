package com.hafiztaruligani.cryptoday.domain.usecase.user

import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import javax.inject.Inject

class SetUserName @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(value: String) = userRepository.setUserName(value)
}
