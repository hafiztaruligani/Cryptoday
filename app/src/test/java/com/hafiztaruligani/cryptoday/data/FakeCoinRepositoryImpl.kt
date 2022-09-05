package com.hafiztaruligani.cryptoday.data

import androidx.paging.PagingData
import com.hafiztaruligani.cryptoday.data.local.entity.CoinEntity
import com.hafiztaruligani.cryptoday.domain.model.CoinDetail
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class FakeCoinRepositoryImpl: CoinRepository {


    private fun getCoin(coinId: String): CoinEntity {
        fun getRandomString() = UUID.randomUUID().toString()
        return CoinEntity(
            id = coinId,
            symbol = getRandomString(),
            name = getRandomString(),
            image = getRandomString(),
            marketData = MarketData(
                timeUnit = getRandomString(),
                marketCapRank = Random().nextInt(),
                currentPrice = Random().nextDouble(),
                priceChangePercentage = Random().nextDouble(),
                low = Random().nextDouble(),
                high = Random().nextDouble(),
                marketCap = Random().nextDouble(),
                circulatingSupply = Random().nextDouble(),
                maxSupply = Random().nextDouble(),
                pair = getRandomString(),
                lastUpdate = getRandomString(),
            ),
            info = CoinDetail(
                genesisDate = getRandomString(),
                link = getRandomString(),
                description = getRandomString()
            ),
        )
    }


    override fun getCoinsPaged(): Flow<PagingData<CoinEntity>> {
        return flow {  }
    }

    override fun getCoinWithDetail(coinId: String): Flow<CoinEntity> {
        return flow {
            repeat(5){
                emit(getCoin(coinId))
            }
        }
    }
}