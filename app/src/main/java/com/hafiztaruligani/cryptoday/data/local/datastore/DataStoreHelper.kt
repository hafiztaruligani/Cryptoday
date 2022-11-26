package com.hafiztaruligani.cryptoday.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface DataStoreHelper {

    suspend fun <T> write(key: String, data: T)

    fun <T> read(key: String, clazz: Class<T>): Flow<T>

    suspend fun clear()
}
