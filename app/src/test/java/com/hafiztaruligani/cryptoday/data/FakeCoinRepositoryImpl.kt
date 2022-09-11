package com.hafiztaruligani.cryptoday.data

class FakeCoinRepositoryImpl{/*: CoinRepository {


    private fun getCoin(coinId: String): CoinEntity {
        fun getRandomString() = UUID.randomUUID().toString()
        return CoinEntity(
            coinId = coinId,
            symbol = getRandomString(),
            name = getRandomString(),
            image = getRandomString(),
            marketData = MarketData(
                timeUnit = getRandomString(),
                marketCapRank = Random().nextInt(),
                currentPrice = Random().nextDouble(),
                priceChangePercentage = Random().nextDouble(),
                low = Random().nextDouble(),
                high = Random().nextDouble(),
                marketCap = Random().nextDouble(),
                circulatingSupply = Random().nextDouble(),
                maxSupply = Random().nextDouble(),
                pair = getRandomString(),
                lastUpdate = getRandomString(),
            ),
            info = CoinDetail(
                genesisDate = getRandomString(),
                link = getRandomString(),
                description = getRandomString()
            ),
        )
    }

    override fun getCoinsPaged(): PagingSource<Int, CoinEntity> {
        return
    }

    override suspend fun getCoinsFromNetwork(page: Int, pageSize: Int): List<CoinResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun insertCoins(value: List<CoinEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCoins() {
        TODO("Not yet implemented")
    }

    override suspend fun getCoinRemoteKey(): CoinRemoteKey {
        TODO("Not yet implemented")
    }

    override suspend fun insertCoinRemoteKey(value: CoinRemoteKey) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCoinRemoteKey() {
        TODO("Not yet implemented")
    }

    override fun getCoinWithDetail(coinId: String): Flow<CoinEntity> {
        return flow {
            repeat(5){
                emit(getCoin(coinId))
            }
        }
    }*/
}