package com.hafiztaruligani.cryptoday.domain.usecase.user

import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetUserCurrencyPair @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(value: String){
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.setUserCurrencyPair(value)
        }
    }
}