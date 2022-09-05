package com.hafiztaruligani.cryptoday

import androidx.paging.map
import com.hafiztaruligani.cryptoday.data.remote.ApiService
import com.hafiztaruligani.cryptoday.data.repository.CoinRepositoryImpl
import com.hafiztaruligani.cryptoday.domain.usecase.GetCoinsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getCoinsUsecaseTest()= runTest{/*
        val coinRepository = CoinRepositoryImpl(
            Retrofit
                .Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        )
        val expectedResult= listOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)

        val getCoinsData = GetCoinsUseCase(coinRepository).invoke().first().map { it.marketData.marketCapRank }

        assertEquals(expectedResult,getCoinsData)*/
    }


}