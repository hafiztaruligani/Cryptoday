package com.hafiztaruligani.cryptoday.domain.repository.paging

import android.util.Log
import androidx.paging.*
import androidx.paging.LoadType.*
import androidx.room.withTransaction
import com.hafiztaruligani.cryptoday.data.local.AppDatabase
import com.hafiztaruligani.cryptoday.data.local.entity.CoinEntity
import com.hafiztaruligani.cryptoday.data.local.entity.CoinRemoteKey
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class CoinPagingRemoteMediator @Inject constructor(
    private val coinRepository: CoinRepository,
    private val appDatabase: AppDatabase
): RemoteMediator<Int, CoinEntity>() {
    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CoinEntity>
    ): MediatorResult {

        try {
            var loadKey: Int = when(loadType){
                REFRESH -> 1
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND ->{
                    val remoteKey = coinRepository.getCoinRemoteKey()

                    if(remoteKey.nextPage==null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextPage
                }
            }

            val data: List<CoinResponse> = coinRepository.getCoinsFromNetwork(
                page = loadKey,
                pageSize = when (loadType) {
                    REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            )

            appDatabase.withTransaction {
                Log.d(TAG, "load : loadType: $loadType || loadKey: $loadKey")
                if (loadType == REFRESH) {
                   // coinRepository.deleteCoins()
                    coinRepository.deleteCoinRemoteKey()
                }

                loadKey++
                coinRepository.insertCoinRemoteKey(CoinRemoteKey("", loadKey))
                coinRepository.insertCoins(data.map { it.toCoinEntity() })
            }
            return MediatorResult.Success(endOfPaginationReached = data.isEmpty())
        }catch (e: HttpException){
            Log.e(TAG, "load error http: $e")
            return MediatorResult.Error(e)
        }catch (e: IOException){
            Log.e(TAG, "load error IO: $e")
            return MediatorResult.Error(e)
        }catch (e:Exception){
            Log.e(TAG, "load error E: $e")
            return MediatorResult.Error(e)
        }
    }
}