package com.hafiztaruligani.cryptoday.domain.usecase

import android.util.Log
import androidx.paging.*
import com.hafiztaruligani.cryptoday.data.local.entity.CoinRemoteKey
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.repository.paging.CoinPagingRemoteMediator
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.Cons.UPDATE_DELAY_TIME
import com.hafiztaruligani.cryptoday.util.Resource
import com.hafiztaruligani.cryptoday.util.Resource.*
import com.hafiztaruligani.cryptoday.util.Resource.Companion.NETWORK_RESTRICTED
import com.hafiztaruligani.cryptoday.util.Resource.Companion.NETWORK_UNAVAILABLE
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class GetCoinsUseCase @Inject constructor(
    private val coinRepository: CoinRepository,
    private val coinPagingRemoteMediator: CoinPagingRemoteMediator
) {

    private var updateJob : Job? = null

    operator fun invoke(): Flow<PagingData<Coin>> {
        update()
        return flow {
                Pager(
                    config = PagingConfig(
                        initialLoadSize = Cons.PER_PAGE,
                        pageSize = Cons.PER_PAGE,
                        prefetchDistance = Cons.PRE_FETCH_DISTANCE
                    ),
                    remoteMediator = coinPagingRemoteMediator
                ){
                    coinRepository.getCoinsPaged()
                }.flow.collect(){

                    updateJob?.cancel().also { Log.d(TAG, "update: canceling ") }

                    val data: PagingData<Coin> = it.map { coinEntity -> coinEntity.toCoin()}
                    emit(data)

                    if (updateJob?.isActive == false || updateJob == null)
                        update()
                }

        }
    }

    private fun update() {
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
                while(count>0){
                    coinResponse.plusAssign(
                        if(count>maxPageSize) coinRepository.getCoinsFromNetwork(
                            page = page,
                            pageSize = maxPageSize
                        ) else coinRepository.getCoinsFromNetwork(
                            page = ((pageSize-1).div(count)+1),
                            pageSize = count
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
                    coinResponse.map { it.toCoinEntity() }
                )

                update()
            }catch (e: HttpException){
                Log.e(TAG, "load : update: $e")
            }catch (e: IOException){
                Log.e(TAG, "load : update: $e")
            }
        }
    }
}