package com.hafiztaruligani.cryptoday.data.local

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.hafiztaruligani.cryptoday.data.local.entity.CoinDetailEntity
import com.hafiztaruligani.cryptoday.data.local.entity.CoinEntity
import com.hafiztaruligani.cryptoday.data.local.entity.CoinWithDetailEntity
import com.hafiztaruligani.cryptoday.domain.model.Coin
import com.hafiztaruligani.cryptoday.domain.model.CoinDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {

    @Transaction
    @Query("SELECT * FROM coin ORDER BY rank ASC")
    fun getAllCoins(): PagingSource<Int,CoinEntity>

    @Transaction
    @Query("SELECT * FROM coin WHERE coinId=:coinId")
    fun getCoinWithDetailById(coinId: String): Flow<CoinWithDetailEntity>

    @Insert(onConflict = REPLACE)
    suspend fun insertCoins(coinEntity: List<CoinEntity>)

    @Insert(onConflict = REPLACE)
    suspend fun insertCoinDetail(coinDetailEntity: CoinDetailEntity)

    @Query("DELETE FROM coin")
    suspend fun delete()
}
