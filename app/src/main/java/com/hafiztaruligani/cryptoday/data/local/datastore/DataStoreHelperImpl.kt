package com.hafiztaruligani.cryptoday.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_datastore")
class DataStoreHelperImpl(private val context: Context) : DataStoreHelper {

    override suspend fun <T> write(key: String, data: T) {
        context.dataStore.edit {
            when (data) {
                is Boolean -> it[booleanPreferencesKey(key)] = data
                is String -> it[stringPreferencesKey(key)] = data
            }
        }
    }

    override fun <T> read(key: String, clazz: Class<T>): Flow<T> {
        return context.dataStore.data.map {
            when (clazz) {
                Boolean::class.java -> it[booleanPreferencesKey(key)] ?: false
                String::class.java -> it[stringPreferencesKey(key)] ?: ""
                else -> null
            }
        } as Flow<T>
    }

    override suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
