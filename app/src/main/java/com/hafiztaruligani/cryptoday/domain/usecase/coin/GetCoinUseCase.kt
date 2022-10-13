package com.hafiztaruligani.cryptoday.domain.usecase.coin

import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import com.hafiztaruligani.cryptoday.util.Resource
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetCoinUseCase @Inject constructor(private val coinRepository: CoinRepository, private val userRepository: UserRepository) {
    suspend operator fun invoke(coinId: String): Flow<Resource<Coin>> = flow{
        try {
            emit(Resource.Loading())
            coinRepository.getCoin(coinId, "usd").collect() {
               emit( Resource.Success(it.toCoin()))
            }
        }catch (e: HttpException){
            if (e.code()==429) emit(Resource.Error(Resource.NETWORK_RESTRICTED))
            else emit(Resource.Error())
        }catch (e: IOException){
            emit(Resource.Error(Resource.NETWORK_UNAVAILABLE))
        }catch (e: Exception){
            emit(Resource.Error(Resource.NETWORK_UNAVAILABLE))
        }
    }
}