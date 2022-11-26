package com.hafiztaruligani.cryptoday.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinDetailEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinPagingDataEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.FavouriteCoinEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.QueryHistoryEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.RemoteKey

@Database(
    entities = [
        CoinPagingDataEntity::class,
        CoinDetailEntity::class,
        RemoteKey::class,
        FavouriteCoinEntity::class,
        QueryHistoryEntity::class
    ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao
    abstract fun coinRemoteKeyDao(): CoinRemoteKeyDao

    companion object {
        fun getDatabaseInstance(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "coin_database"
        ).build()
    }
}
