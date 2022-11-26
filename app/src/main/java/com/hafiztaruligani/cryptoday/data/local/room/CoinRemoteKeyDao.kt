package com.hafiztaruligani.cryptoday.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.hafiztaruligani.cryptoday.data.local.room.entity.RemoteKey

@Dao
interface CoinRemoteKeyDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE id=:id")
    suspend fun getRemoteKey(id: String): RemoteKey?

    @Query("DELETE FROM remote_keys WHERE id=:id")
    suspend fun delete(id: String)
}
