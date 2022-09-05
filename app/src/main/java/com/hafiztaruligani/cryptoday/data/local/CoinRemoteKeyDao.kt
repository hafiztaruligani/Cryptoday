package com.hafiztaruligani.cryptoday.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.hafiztaruligani.cryptoday.data.local.entity.CoinRemoteKey
import java.nio.channels.SelectionKey

@Dao
interface CoinRemoteKeyDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(coinRemoteKey: CoinRemoteKey)

    @Query("SELECT * FROM remote_keys")
    suspend fun getRemoteKey(): CoinRemoteKey

    @Query("DELETE FROM remote_keys")
    suspend fun delete()

}