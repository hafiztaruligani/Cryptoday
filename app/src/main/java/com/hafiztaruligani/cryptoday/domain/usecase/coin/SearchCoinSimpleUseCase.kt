package com.hafiztaruligani.cryptoday.domain.usecase.coin

import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Resource
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class SearchCoinSimpleUseCase @Inject constructor(
    private val coinRepository: CoinRepository
) {
    suspend operator fun invoke(params: String): Flow<Resource<List<CoinSimple>>> = flow {
        emit(Resource.Loading())
        try {

            val data = coinRepository.searchCoinId(params)
            emit(Resource.Success(data))
        } catch (e: HttpException) {
            emit(Resource.Error())
        } catch (e: IOException) {
            emit(Resource.Error())
        }
    }
}
