package com.hafiztaruligani.cryptoday.domain.usecase

import com.hafiztaruligani.cryptoday.data.FakeCoinRepositoryImpl
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class GetCoinWithDetailUseCaseTest{

    lateinit var coinRepository: CoinRepository
    lateinit var useCase: GetCoinWithDetailUseCase
    @Before
    fun setup(){
        coinRepository = FakeCoinRepositoryImpl()
        useCase = GetCoinWithDetailUseCase(coinRepository)
    }

    @Test
    fun `make sure that coins flow updated`() = runTest{
        val id = UUID.randomUUID().toString()
        var oldValue : Coin? = null
        useCase.invoke(id).collect(){
            if(oldValue!=null){
                assertNotEquals(it, oldValue)
            }
            oldValue = it
            println("coinId = ${it.id} coinPrice = ${it.marketData.currentPrice}")
        }
    }

}