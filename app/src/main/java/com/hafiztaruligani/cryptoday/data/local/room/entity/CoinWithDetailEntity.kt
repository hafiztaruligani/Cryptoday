package com.hafiztaruligani.cryptoday.data.local.room.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CoinWithDetailEntity(
    @Embedded
    val coinPagingDataEntity: CoinPagingDataEntity,
    @Relation(
        parentColumn = "coinId",
        entityColumn = "coinId"
    )
    val coinDetailEntity: CoinDetailEntity
)
