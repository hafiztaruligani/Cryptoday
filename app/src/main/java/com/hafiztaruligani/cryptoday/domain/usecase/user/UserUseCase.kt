package com.hafiztaruligani.cryptoday.domain.usecase.user

import javax.inject.Inject

data class UserUseCase @Inject constructor(
    val getUserCurrencyPair: GetUserCurrencyPair,
    val getUserName: GetUserName,
    val setUserName: SetUserName,
    val setUserCurrencyPair: SetUserCurrencyPair,
    val logout: Logout
)
