package com.hafiztaruligani.cryptoday.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun setUserName(value: String)
    fun getUserName(): Flow<String>

    suspend fun setUserCurrencyPair(value: String)
    fun getUserCurrencyPair(): Flow<String>
    fun logout()
}
