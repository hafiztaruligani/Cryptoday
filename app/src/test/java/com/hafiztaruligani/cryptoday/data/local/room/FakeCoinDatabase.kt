package com.hafiztaruligani.cryptoday.data.local.room

import androidx.paging.PagingSource
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinDetailEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinPagingDataEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.FavouriteCoinEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.QueryHistoryEntity
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCoinDatabase : CoinDao {

    val queryHistoryEntity = mutableListOf<QueryHistoryEntity>()

    init {
        // create fake queryHistory Entity
        repeat(5) { index ->
            queryHistoryEntity.add(
                QueryHistoryEntity(
                    query = UUID.randomUUID().toString(),
                    id = index
                )
            )
        }
    }

    override fun getCoinPagingData(): PagingSource<Int, CoinPagingDataEntity> {
        TODO("Not yet implemented")
    }

    override fun getCoinPagingDataByQuery(params: String):
        PagingSource<Int, CoinPagingDataEntity> {
        TODO("Not yet implemented")
    }

    override fun getCoinWithDetailById(coinId: String): Flow<CoinWithDetailEntity> {
        TODO("Not yet implemented")
    }

    override fun getCoinPagingDataById(coinId: String): Flow<CoinPagingDataEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCoinPagingData(coinId: String, marketData: MarketData) {
        TODO("Not yet implemented")
    }

    override suspend fun insertCoinPagingData(coinPagingDataEntity: List<CoinPagingDataEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun insertCoinDetail(coinDetailEntity: CoinDetailEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCoinPagingData() {
        TODO("Not yet implemented")
    }

    override fun getAllFavourite(): Flow<List<FavouriteCoinEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCoinFavourite(coinId: String, value: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavouriteById(param: String) {
        TODO("Not yet implemented")
    }

    override suspend fun insertFavouriteCoin(value: FavouriteCoinEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavourite() {
        TODO("Not yet implemented")
    }

    // query history
    override fun getQueryHistory(): Flow<List<QueryHistoryEntity>> = flow {
        emit(queryHistoryEntity)
    }

    override suspend fun insertQueryHistory(queryHistoryEntity: QueryHistoryEntity) {
        this.queryHistoryEntity.add(queryHistoryEntity)
    }
}
