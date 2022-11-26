package com.hafiztaruligani.cryptoday.data.local.room

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinDetailEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinPagingDataEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.FavouriteCoinEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.QueryHistoryEntity
import com.hafiztaruligani.cryptoday.domain.model.MarketData
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    // coin paging data
    @Transaction
    @Query("SELECT * FROM coin_paging_data ")
    fun getCoinPagingData(): PagingSource<Int, CoinPagingDataEntity>

    @Transaction
    @Query(
        "SELECT * FROM coin_paging_data " +
            "WHERE coinId LIKE '%' || :params || '%' " +
            "OR symbol LIKE '%' || :params || '%'"
    )
    fun getCoinPagingDataByQuery(params: String): PagingSource<Int, CoinPagingDataEntity>

    @Query("SELECT * FROM coin_paging_data WHERE coinId=:coinId")
    fun getCoinPagingDataById(coinId: String): Flow<CoinPagingDataEntity>

    @Query("UPDATE coin_paging_data SET market_data=:marketData WHERE coinId=:coinId")
    suspend fun updateCoinPagingData(coinId: String, marketData: MarketData)

    @Insert(onConflict = REPLACE)
    suspend fun insertCoinPagingData(coinPagingDataEntity: List<CoinPagingDataEntity>)

    @Query("DELETE FROM coin_paging_data")
    suspend fun deleteCoinPagingData()

    // coin detail
    @Transaction
    @Query("SELECT * FROM coin_paging_data WHERE coinId=:coinId")
    fun getCoinWithDetailById(coinId: String): Flow<CoinWithDetailEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insertCoinDetail(coinDetailEntity: CoinDetailEntity)

    // coin favorite
    @Query("SELECT * FROM favourite")
    fun getAllFavourite(): Flow<List<FavouriteCoinEntity>>

    @Query("UPDATE coin_paging_data SET favourite =:value WHERE coinId LIKE '%' || :coinId || '%'")
    suspend fun updateCoinFavourite(coinId: String, value: Boolean)

    @Query("DELETE FROM favourite WHERE coin_id LIKE '%' || :param || '%'")
    suspend fun deleteFavouriteById(param: String)

    @Insert(onConflict = REPLACE)
    suspend fun insertFavouriteCoin(value: FavouriteCoinEntity)

    @Query("DELETE FROM favourite")
    suspend fun deleteFavourite()

    // coin search history
    @Query("SELECT * FROM query_history ORDER BY id ASC")
    fun getQueryHistory(): Flow<List<QueryHistoryEntity?>?>?

    @Insert(onConflict = REPLACE)
    suspend fun insertQueryHistory(queryHistoryEntity: QueryHistoryEntity)
}
