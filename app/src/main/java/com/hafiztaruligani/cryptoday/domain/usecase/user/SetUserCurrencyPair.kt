package com.hafiztaruligani.cryptoday.domain.usecase.user

import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetUserCurrencyPair @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.setUserCurrencyPair(value)
        }
    }
}
