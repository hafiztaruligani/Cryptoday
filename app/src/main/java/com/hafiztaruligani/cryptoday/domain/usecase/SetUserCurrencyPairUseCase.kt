package com.hafiztaruligani.cryptoday.domain.usecase

import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetUserCurrencyPairUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(value: String){
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.setUserCurrencyPair(value)
        }
    }
}