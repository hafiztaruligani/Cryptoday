package com.hafiztaruligani.cryptoday.domain.usecase.coin

import com.hafiztaruligani.cryptoday.domain.model.CoinWithDetail
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Resource
import com.hafiztaruligani.cryptoday.util.Resource.*
import com.hafiztaruligani.cryptoday.util.Resource.Companion.NETWORK_RESTRICTED
import com.hafiztaruligani.cryptoday.util.Resource.Companion.NETWORK_UNAVAILABLE
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class GetCoinWithDetailUseCase @Inject constructor(private val coinRepository: CoinRepository) {

    suspend operator fun invoke(coinId: String): Flow<Resource<CoinWithDetail>> = flow {
        emit(Loading())
        try {
            coinRepository.getCoinWithDetail(coinId).collect() {
                emit(
                    Success(
                        CoinWithDetail(
                            coin = it.coinPagingDataEntity.toCoin(),
                            detail = it.coinDetailEntity.toCoinDetail(),
                        ),
                    )
                )
            }
        } catch (e: HttpException) {
            if (e.code() == 429) emit(Error(NETWORK_RESTRICTED))
            else emit(Error())
        } catch (e: IOException) {
            emit(Error(NETWORK_UNAVAILABLE))
        } catch (e: Exception) {
            emit(Error(NETWORK_UNAVAILABLE))
        }
    }
}
