package com.hafiztaruligani.cryptoday.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hafiztaruligani.cryptoday.domain.model.CoinDetail

@Entity
data class CoinDetailEntity (
    @PrimaryKey(autoGenerate = false)
    val coinId: String,

    val genesisDate: String,

    val link: String,

    val description: String,
){
    fun toCoinDetail(): CoinDetail{
        return CoinDetail(
            genesisDate= genesisDate,
            link= link,
            description= description,
        )
    }
}