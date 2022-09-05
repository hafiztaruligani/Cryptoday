package com.hafiztaruligani.cryptoday.domain.usecase

import android.util.Log
import androidx.paging.*
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

    operator fun invoke(): Flow<Resource<PagingData<Coin>>> {
        val scope2 = CoroutineScope(Dispatchers.IO)
        update(scope2)
        return flow {
            emit(Loading())
            try {
                Pager(
                    config = PagingConfig(
                        initialLoadSize = Cons.PER_PAGE,
                        pageSize = Cons.PER_PAGE
                    ),
                    remoteMediator = coinPagingRemoteMediator
                ){
                    coinRepository.getCoinsPaged()
                }.flow.collect(){

                    updateJob?.cancel().also { Log.d(TAG, "update: canceling ") }

                    val data: PagingData<Coin> = it.map { coinEntity -> coinEntity.toCoin()}
                    emit(Success(data))

                    if (updateJob?.isActive == false || updateJob == null)
                        update(scope2)
                }

            }catch (e: HttpException){
                if(e.code()==429) emit(Error(NETWORK_RESTRICTED))
                else emit(Error())
                Log.e(TAG, "load error invoke http: ${e.code()} || ${e.localizedMessage}")
            }catch (e: IOException){
                emit(Error(NETWORK_UNAVAILABLE))
                Log.e(TAG, "load error invoke exception: ${e.message}")
            }catch (e: Exception){
                emit(Error(NETWORK_UNAVAILABLE))
            }
        }
    }

    private fun update(scope: CoroutineScope) {
        updateJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(UPDATE_DELAY_TIME)

                yield()
                val nextPage = coinRepository.getCoinRemoteKey()

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

                    if(pageSize>2000){
                        delay(2000)
                    }
                }

                yield()
                coinRepository.insertCoins(
                    coinResponse.map { it.toCoinEntity() }
                )

                update(scope)
            }catch (e: HttpException){
                Log.e(TAG, "load : update: $e")
            }catch (e: IOException){
                Log.e(TAG, "load : update: $e")
            }
        }
    }
}