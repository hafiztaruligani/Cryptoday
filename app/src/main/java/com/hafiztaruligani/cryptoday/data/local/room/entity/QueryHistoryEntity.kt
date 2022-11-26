package com.hafiztaruligani.cryptoday.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "query_history")
data class QueryHistoryEntity(

    val query: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0

)
