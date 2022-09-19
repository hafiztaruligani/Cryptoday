package com.hafiztaruligani.cryptoday.data.local.room

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinDetailEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.domain.model.Coin
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    @Transaction
    @Query("SELECT * FROM coin")//" ORDER BY rank ASC")
    fun getAllCoins(): PagingSource<Int,CoinEntity>

    @Transaction
    @Query("SELECT * FROM coin WHERE coinId LIKE '%' || :params || '%' OR symbol LIKE '%' || :params || '%'")//" ORDER BY rank ASC")
    fun getAllCoinsWithParams(params: String): PagingSource<Int,CoinEntity>

    @Transaction
    @Query("SELECT * FROM coin WHERE coinId=:coinId")
    fun getCoinWithDetailById(coinId: String): Flow<CoinWithDetailEntity>


    @Query("SELECT * FROM coin WHERE coinId=:coinId")
    fun getCoinById(coinId: String): Flow<CoinEntity>


    @Insert(onConflict = REPLACE)
    suspend fun insertCoins(coinEntity: List<CoinEntity>)

    @Insert(onConflict = REPLACE)
    suspend fun insertCoinDetail(coinDetailEntity: CoinDetailEntity)

    @Query("DELETE FROM coin")
    suspend fun delete()
}
