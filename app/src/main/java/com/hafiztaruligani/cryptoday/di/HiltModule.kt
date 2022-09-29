package com.hafiztaruligani.cryptoday.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.hafiztaruligani.cryptoday.BuildConfig
import com.hafiztaruligani.cryptoday.data.local.datastore.DataStoreHelper
import com.hafiztaruligani.cryptoday.data.local.room.AppDatabase
import com.hafiztaruligani.cryptoday.data.local.room.CoinDao
import com.hafiztaruligani.cryptoday.data.local.room.CoinRemoteKeyDao
import com.hafiztaruligani.cryptoday.data.remote.ApiService
import com.hafiztaruligani.cryptoday.data.repository.CoinRepositoryImpl
import com.hafiztaruligani.cryptoday.data.repository.UserRepositoryImpl
import com.hafiztaruligani.cryptoday.domain.repository.CoinRepository
import com.hafiztaruligani.cryptoday.domain.repository.UserRepository
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
        coinRemoteKeyDao: CoinRemoteKeyDao,
        dataStoreHelper: DataStoreHelper,
        userRepository: UserRepository,
        firestore: FirebaseFirestore
    ): CoinRepository{
        return CoinRepositoryImpl(
            apiService = apiService,
            coinDao = coinDao,
            coinRemoteKeyDao = coinRemoteKeyDao,
            dataStoreHelper = dataStoreHelper,
            userRepository = userRepository,
            firestore = firestore
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(dataStoreHelper: DataStoreHelper): UserRepository = UserRepositoryImpl(dataStoreHelper)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

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
    fun provideFirestore(@ApplicationContext context: Context): FirebaseFirestore {
        context.apply { return FirebaseFirestore.getInstance() }
    }
}
