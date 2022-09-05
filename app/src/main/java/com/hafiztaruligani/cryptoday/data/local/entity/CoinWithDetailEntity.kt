package com.hafiztaruligani.cryptoday.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.hafiztaruligani.cryptoday.domain.model.Coin


data class CoinWithDetailEntity(
    @Embedded
    val coinEntity: CoinEntity,
    @Relation(
        parentColumn = "coinId",
        entityColumn = "coinId"
    )
    val coinDetailEntity: CoinDetailEntity
)