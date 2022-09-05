package com.hafiztaruligani.cryptoday.di

import android.content.Context
import com.hafiztaruligani.cryptoday.BuildConfig
import com.hafiztaruligani.cryptoday.data.DataStoreHelper
import com.hafiztaruligani.cryptoday.data.local.AppDatabase
import com.hafiztaruligani.cryptoday.data.local.CoinDao
import com.hafiztaruligani.cryptoday.data.local.CoinRemoteKeyDao
import com.hafiztaruligani.cryptoday.data.remote.ApiService
import com.hafiztaruligani.cryptoday.data.repository.CoinRepositoryImpl
import com.hafiztaruligani.cryptoday.data.repository.UserRepositoryImpl
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
import com.hafiztaruligani.cryptoday.domain.repository.paging.CoinPagingRemoteMediator
import com.hafiztaruligani.cryptoday.domain.usecase.GetCoinsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun provideApi(): ApiService {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient().newBuilder()
            .addNetworkInterceptor(logger)
            .build()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideCoinRepository(
        apiService: ApiService,
        coinDao: CoinDao,
        coinRemoteKeyDao: CoinRemoteKeyDao
    ): CoinRepository{
        return CoinRepositoryImpl(
            apiService = apiService,
            coinDao = coinDao,
            coinRemoteKeyDao = coinRemoteKeyDao
        )
    }

    @Provides
    @Singleton
    fun provideGetCoinsUseCase(
        coinRepository: CoinRepository,
        mediator: CoinPagingRemoteMediator
    ): GetCoinsUseCase{
        return GetCoinsUseCase(coinRepository, mediator)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideCoinDao(appDatabase: AppDatabase): CoinDao =appDatabase.coinDao()

    @Provides
    @Singleton
    fun provideCoinRemoteKeyDao(appDatabase: AppDatabase): CoinRemoteKeyDao =appDatabase.coinRemoteKeyDao()

    @Provides
    @Singleton
    fun provideDataStoreHelper(@ApplicationContext context: Context): DataStoreHelper = DataStoreHelper(context)

    @Provides
    @Singleton
    fun provideUserRepository(dataStoreHelper: DataStoreHelper): UserRepository = UserRepositoryImpl(dataStoreHelper)
}
