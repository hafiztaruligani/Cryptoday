package com.hafiztaruligani.cryptoday.data.paging

import androidx.paging.*
import androidx.paging.LoadType.*
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinPagingDataEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.RemoteKey
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.usecase.coin.CoinsOrder
import java.io.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class CoinPagingRemoteMediator(
    private val coinRepository: CoinRepository,
    private val coinsOrder: CoinsOrder
) : RemoteMediator<Int, CoinPagingDataEntity>() {

    companion object { const val REMOTE_KEY_ID = "coins_remote_key" }
    private var searchResult: List<String> = listOf()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CoinPagingDataEntity>
    ): MediatorResult {
        try {

            val loadKey: Int = when (loadType) {
                REFRESH -> {
                    searchResult = search(coinsOrder.query)
                    1
                }
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                APPEND -> {
                    coinRepository.getCoinRemoteKey(REMOTE_KEY_ID)?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val dataNotFound = coinsOrder.query.isNotBlank() && searchResult.isEmpty()
            coinsOrder.ids = searchResult

            return if (dataNotFound)
                MediatorResult.Success(endOfPaginationReached = true)
            else {
                val remoteData: List<CoinResponse> = getRemoteData(loadKey, loadType, state)
                insertData(loadType, remoteData, loadKey)
                MediatorResult.Success(endOfPaginationReached = remoteData.isEmpty())
            }
        } catch (e: HttpException) {
            println("load error http: $e")
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            println("load error IO: $e")
            return MediatorResult.Error(e)
        }
    }

    private suspend fun search(query: String) =
        if (query.isBlank()) listOf()
        else coinRepository.searchCoinId(query).map { it.id }

    private suspend fun getRemoteData(
        loadKey: Int,
        loadType: LoadType,
        state: PagingState<Int, CoinPagingDataEntity>
    ) = coinRepository.getCoinsRemote(
        page = loadKey,
        pageSize = when (loadType) {
            REFRESH -> state.config.initialLoadSize
            else -> state.config.pageSize
        },
        vsCurrencies = coinsOrder.currencyPair,
        sortBy = coinsOrder.sortBy.apiString,
        ids = coinsOrder.ids
    )

    private suspend fun insertData(
        loadType: LoadType,
        data: List<CoinResponse>,
        loadKey: Int
    ) {
        if (loadType == REFRESH) deleteOldData()
        coinRepository.insertCoins(
            data.map { it.toCoinEntity(currencyPair = coinsOrder.currencyPair) }
        )
        coinRepository.insertCoinRemoteKey(RemoteKey(REMOTE_KEY_ID, loadKey + 1))
    }

    private suspend fun deleteOldData() {
        coinRepository.deleteCoins()
        coinRepository.deleteCoinRemoteKey(remoteKeyId = REMOTE_KEY_ID)
    }
}
