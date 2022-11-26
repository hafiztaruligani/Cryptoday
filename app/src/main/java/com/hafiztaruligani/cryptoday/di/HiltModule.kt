package com.hafiztaruligani.cryptoday.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.hafiztaruligani.cryptoday.BuildConfig
import com.hafiztaruligani.cryptoday.data.local.datastore.DataStoreHelper
import com.hafiztaruligani.cryptoday.data.local.datastore.DataStoreHelperImpl
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
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    // Remote API config
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

    // coin repository
    @Provides
    @Singleton
    fun provideCoinRepository(
        apiService: ApiService,
        coinDao: CoinDao,
        coinRemoteKeyDao: CoinRemoteKeyDao,
        dataStoreHelper: DataStoreHelper,
        userRepository: UserRepository,
        fireStore: FirebaseFirestore
    ): CoinRepository {
        return CoinRepositoryImpl(
            apiService = apiService,
            coinDao = coinDao,
            coinRemoteKeyDao = coinRemoteKeyDao,
            dataStoreHelper = dataStoreHelper,
            userRepository = userRepository,
            fireStore = fireStore
        )
    }

    // user repository
    @Provides
    @Singleton
    fun provideUserRepository(dataStoreHelper: DataStoreHelper): UserRepository = UserRepositoryImpl(dataStoreHelper)

    // app database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getDatabaseInstance(context)

    // provide coin database
    @Provides
    @Singleton
    fun provideCoinDao(appDatabase: AppDatabase): CoinDao =
        appDatabase.coinDao()

    // provide remote key database
    @Provides
    @Singleton
    fun provideCoinRemoteKeyDao(appDatabase: AppDatabase): CoinRemoteKeyDao =
        appDatabase.coinRemoteKeyDao()

    @Provides
    @Singleton
    fun provideDataStoreHelper(@ApplicationContext context: Context): DataStoreHelper = DataStoreHelperImpl(context)

    @Provides
    @Singleton
    fun provideFireStore(@ApplicationContext context: Context): FirebaseFirestore {
        context.apply { return FirebaseFirestore.getInstance() }
    }
}
