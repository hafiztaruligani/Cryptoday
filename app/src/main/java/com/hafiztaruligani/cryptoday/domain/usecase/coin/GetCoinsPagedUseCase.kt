package com.hafiztaruligani.cryptoday.domain.usecase.coin

import android.util.Log
import androidx.paging.*
import com.hafiztaruligani.cryptoday.data.paging.CoinPagingRemoteMediator.Companion.REMOTE_KEY_ID
import com.hafiztaruligani.cryptoday.data.remote.dto.CoinResponse
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Cons
import com.hafiztaruligani.cryptoday.util.Cons.MAX_PAGE_SIZE
import com.hafiztaruligani.cryptoday.util.Cons.TAG
import com.hafiztaruligani.cryptoday.util.Cons.UPDATE_DELAY_TIME
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
@Singleton
class GetCoinsPagedUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {

    /**
     * when application initiation, current cached paging data is must be default data.
     * default paging data is when query is blank.
     * so when this class is initiated and last query history is not blank,
     * current cached paging data is not default data and need to be delete/clear
     */
    init {
        runBlocking {
            val queryHistory = coinRepository.getQueryHistory()?.firstOrNull()
            val lastQuery = queryHistory?.lastOrNull()?.query

            if (lastQuery?.isNotBlank() == true)
                deleteOldData()
        }
    }

    private var updateJob: Job? = null

    suspend operator fun invoke(coinsOrder: CoinsOrder): Flow<PagingData<Coin>> {

        coinRepository.insertQueryHistory(coinsOrder.query)
        update(coinsOrder)

        return coinRepository.getCoinsPaged(coinsOrder).map { data ->
            data.map { coinEntity -> coinEntity.toCoin() }
        }
    }

    /**
     * updating cached coins data with delay time (UPDATE_DELAY_TIME)
     * to avoid http exception (TOO MANY REQUEST).
     * if old job is unfinished, then cancel old job.
     */
    private suspend fun update(coinsOrder: CoinsOrder) {

        updateJob?.cancel()

        updateJob = CoroutineScope(Dispatchers.IO).launch {
            try {

                delay(UPDATE_DELAY_TIME)

                yield()
                val updatedData = getUpdatedData(coinsOrder)
                yield()

                insert(updatedData, coinsOrder)

                update(coinsOrder)
            } catch (e: HttpException) {
                Log.e(TAG, "load : update: $e")
            } catch (e: IOException) {
                Log.e(TAG, "load : update: $e")
            }
        }
    }

    /**
     * remote API maximum page size is 250, so if coins data more then 250,
     * have to request gradually (250 data of each request)
     */
    private suspend fun getUpdatedData(coinsOrder: CoinsOrder): MutableList<CoinResponse> {
        val newUpdateData = mutableListOf<CoinResponse>()
        var currentPage = 1

        // get remote next key to count actual total data
        val remoteKey = coinRepository.getCoinRemoteKey(REMOTE_KEY_ID)?.nextPage
        val totalData = remoteKey?.takeIf { it> 1 }?.let {
            (it - 1) * Cons.PAGE_SIZE
        } ?: Cons.PAGE_SIZE

        var counter = totalData

        // repeat until counter = 0
        while (counter > 0) {

            var page: Int
            var pageSize: Int

            if (counter > MAX_PAGE_SIZE) {
                page = currentPage
                pageSize = MAX_PAGE_SIZE
            } else {
                page = ((totalData - 1).div(counter) + 1)
                pageSize = counter
            }

            // get remote data
            newUpdateData.plusAssign(
                coinRepository.getCoinsRemote(
                    page = page,
                    pageSize = pageSize,
                    vsCurrencies = coinsOrder.currencyPair,
                    sortBy = coinsOrder.sortBy.apiString,
                    ids = coinsOrder.ids
                )
            )

            // increase page and decrease requested data counter
            currentPage++
            counter -= MAX_PAGE_SIZE

            // add delay time again if data more then 250 to avoid http exception (TOO MANY REQUEST)
            if (totalData > MAX_PAGE_SIZE) {
                delay(UPDATE_DELAY_TIME.takeIf { it> 1 }?.div(5) ?: 2000)
            }
        }
        return newUpdateData
    }

    private suspend fun insert(
        coinResponse: MutableList<CoinResponse>,
        coinsOrder: CoinsOrder
    ) {
        coinResponse.forEach { data ->
            data.id?.let {
                coinRepository.updateMarketData(
                    data.id,
                    data.getMarketData(currencyPair = coinsOrder.currencyPair)
                )
            }
        }
    }

    private suspend fun deleteOldData() {
        coinRepository.deleteCoins()
        coinRepository.deleteCoinRemoteKey(remoteKeyId = REMOTE_KEY_ID)
    }
}
