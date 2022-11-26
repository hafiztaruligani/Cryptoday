package com.hafiztaruligani.cryptoday.domain.usecase

import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.util.Resource
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class InitGlobalDataUseCase @Inject constructor(val coinRepository: CoinRepository) {
    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            coinRepository.insertPair()
            emit(Resource.Success(true))
        } catch (e: HttpException) {
            emit(Resource.Error())
        } catch (e: IOException) {
            emit(Resource.Error())
        } catch (e: Exception) {
            emit(Resource.Error())
        }
    }
}
