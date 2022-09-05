package com.hafiztaruligani.cryptoday.data.repository

import com.hafiztaruligani.cryptoday.data.DataStoreHelper
import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl (private val dataStoreHelper: DataStoreHelper): UserRepository {

    companion object{
        private const val USER_NAME_KEY = "user_name_key"
    }

    override suspend fun setUserName(value: String) {
        dataStoreHelper.write(USER_NAME_KEY, value)
    }

    override fun getUserName(): Flow<String> {
        return dataStoreHelper.read(USER_NAME_KEY, String::class.java)
    }
}