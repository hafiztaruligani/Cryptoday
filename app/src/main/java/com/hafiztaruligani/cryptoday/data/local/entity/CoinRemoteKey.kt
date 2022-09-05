package com.hafiztaruligani.cryptoday.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "remote_keys")
data class CoinRemoteKey(
    @PrimaryKey
    val id: String,
    val nextPage: Int?
){

}
