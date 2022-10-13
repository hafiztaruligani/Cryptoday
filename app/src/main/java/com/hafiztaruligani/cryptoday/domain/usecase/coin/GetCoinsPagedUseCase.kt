package com.hafiztaruligani.cryptoday.domain.usecase.coin

import android.util.Log
import androidx.paging.*
import com.hafiztaruligani.cryptoday.data.local.room.AppDatabase
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinRemoteKey
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.repository.paging.CoinPagingRemoteMediator
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.Cons.UPDATE_DELAY_TIME
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class GetCoinsPagedUseCase @Inject constructor(
    private val coinRepository: CoinRepository,
    val appDatabase: AppDatabase
) {

    private var updateJob : Job? = null

    operator fun invoke(coinsOrder: CoinsOrder): Flow<PagingData<Coin>> {
        update(coinsOrder)
        val coinPagingRemoteMediator= CoinPagingRemoteMediator(coinRepository, appDatabase, coinsOrder)
        return flow {
            Pager(
                config = PagingConfig(
                    initialLoadSize = Cons.PER_PAGE,
                    pageSize = Cons.PER_PAGE,
                    prefetchDistance = Cons.PRE_FETCH_DISTANCE
                ),
                remoteMediator = coinPagingRemoteMediator
            ){
                coinRepository.getCoinsPaged(coinsOrder)
            }.flow.collect(){

                updateJob?.cancel().also { Log.d(TAG, "update: canceling ") }

                val data: PagingData<Coin> = it.map { coinEntity -> coinEntity.toCoin()}
                emit(data)

                if (updateJob?.isActive == false || updateJob == null)
                    update(coinsOrder)
            }

        }
    }

    private fun update(coinsOrder: CoinsOrder) {
        updateJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(UPDATE_DELAY_TIME)

                yield()
                val nextPage: CoinRemoteKey? = coinRepository.getCoinRemoteKey()

                var pageSize = Cons.PER_PAGE
                val maxPageSize = 250 //valid pageSize range -> 1..250
                nextPage?.nextPage?.let {
                    pageSize = (it-1) * Cons.PER_PAGE
                }

                val coinResponse = mutableListOf<CoinResponse>()
                var page = 1
                var count = pageSize
                while (false){//(count>0){
                    var p1 = 0
                    var p2= 0
                    if(count>maxPageSize) {
                        p1 = page
                        p2 = maxPageSize
                    }
                    else {
                        p1 = ((pageSize - 1).div(count) + 1)
                        p2 = count
                    }
                    coinResponse.plusAssign(
                        coinRepository.getCoinsFromNetwork(
                            page = p1,
                            pageSize = p2,
                            vsCurrencies = coinsOrder.currencyPair,
                            sortBy = coinsOrder.sortBy.apiString,
                            ids = coinsOrder.ids
                        )
                    )

                    page++
                    count-=maxPageSize

                    if(pageSize>250){
                        delay(2000)
                    }
                }

                yield()
                coinRepository.insertCoins(
                    coinResponse.map { it.toCoinEntity(currencyPair = coinsOrder.currencyPair) }
                )

                update(coinsOrder)
            }catch (e: HttpException){
                Log.e(TAG, "load : update: $e")
            }catch (e: IOException){
                Log.e(TAG, "load : update: $e")
            }
        }
    }
}