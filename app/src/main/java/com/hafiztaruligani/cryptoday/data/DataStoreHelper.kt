package com.hafiztaruligani.cryptoday.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_datastore")
class DataStoreHelper(private val context: Context) {


    suspend fun <T> write(key : String, data : T){
        context.dataStore.edit {
            when(data){
                is Boolean -> it[booleanPreferencesKey(key)]=data
                is String -> it[stringPreferencesKey(key)]=data
            }
        }
    }

    fun <T> read(key:String, clazz: Class<T>): Flow<T> {
        return context.dataStore.data.map {
            when (clazz){
                Boolean::class.java -> it[booleanPreferencesKey(key)]?:false
                String::class.java -> it[stringPreferencesKey(key)]?:""
                else -> null
            }
        } as Flow<T>
    }

    fun <T> readOnce(key: String, clazz: Class<T>): T {
        return context.dataStore.data.map {
            when (clazz){
                Boolean::class.java -> it[booleanPreferencesKey(key)]?:false
                String::class.java -> it[stringPreferencesKey(key)]?:""
                else -> null
            }
        } as T
    }

    suspend fun clear(){
        context.dataStore.edit {
            it.clear()
        }
    }
}