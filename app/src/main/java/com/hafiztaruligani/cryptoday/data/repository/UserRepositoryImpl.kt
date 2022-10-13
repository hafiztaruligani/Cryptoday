package com.hafiztaruligani.cryptoday.data.repository


import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hafiztaruligani.cryptoday.data.local.datastore.DataStoreHelper
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepositoryImpl (
    private val dataStoreHelper: DataStoreHelper
): UserRepository {

    companion object{
        private const val USER_NAME_KEY = "user_name_key"
        private const val USER_CURRENCY_PAIR = "user_currency_pair"
    }

    override fun logout(){
        CoroutineScope(Dispatchers.Unconfined).launch {
            Firebase.auth.signOut()
            dataStoreHelper.clear()
        }
    }

    override suspend fun setUserName(value: String) {
        dataStoreHelper.write(USER_NAME_KEY, value)
    }

    override fun getUserName(): Flow<String> {
        return dataStoreHelper.read(USER_NAME_KEY, String::class.java)
    }

    override suspend fun setUserCurrencyPair(value: String) {
        dataStoreHelper.write(USER_CURRENCY_PAIR, value)
    }

    override fun getUserCurrencyPair(): Flow<String> {
        var before = "usd" //also as default
        var initial = true
        return flow {
            dataStoreHelper.read(USER_CURRENCY_PAIR, String::class.java).collect() {
                if(it.isBlank()) emit(before)
                else if (it!=before){
                    emit(it)
                    before = it
                }else if (initial){
                    initial = false
                    emit(before)
                }

            }
        }
    }
}