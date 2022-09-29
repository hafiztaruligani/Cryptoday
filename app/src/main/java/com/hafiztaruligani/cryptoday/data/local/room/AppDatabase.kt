package com.hafiztaruligani.cryptoday.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinDetailEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinEntity
import com.hafiztaruligani.cryptoday.data.local.room.entity.CoinRemoteKey
import com.hafiztaruligani.cryptoday.data.local.room.entity.FavouriteCoinEntity

@Database(
    entities = [
        CoinEntity::class,
        CoinDetailEntity::class,
        CoinRemoteKey::class,
        FavouriteCoinEntity::class
    ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun coinRemoteKeyDao(): CoinRemoteKeyDao

    companion object{
        fun getInstance(context: Context): AppDatabase = Room.databaseBuilder(context,
            AppDatabase::class.java, "coin_database").build()
    }
}