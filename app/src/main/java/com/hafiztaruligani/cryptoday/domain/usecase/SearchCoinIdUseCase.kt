package com.hafiztaruligani.cryptoday.domain.usecase

import com.hafiztaruligani.cryptoday.domain.model.CoinSimple
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class SearchCoinIdUseCase @Inject constructor(
    val coinRepository: CoinRepository
) {
    suspend operator fun invoke(params: String): Flow<Resource<List<CoinSimple>>> = flow {
        emit(Resource.Loading())
        try {
            coinRepository.insertPair()
            emit(Resource.Success(coinRepository.searchCoinId(params)))
        }catch (e: HttpException){
            emit(Resource.Error())
        }catch (e: IOException){
            emit(Resource.Error())
        }catch (e: Exception){
            emit(Resource.Error())
        }

    }
}