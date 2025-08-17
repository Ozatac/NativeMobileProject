package com.ozatactunahan.nativemobileapp.di

import com.ozatactunahan.nativemobileapp.data.remote.ProductApiService
import com.ozatactunahan.nativemobileapp.data.repository.ProductRepositoryImpl
import com.ozatactunahan.nativemobileapp.domain.repository.ProductRepository
import com.ozatactunahan.nativemobileapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ProductApiService
    ): ProductRepository {
        return ProductRepositoryImpl(apiService)
    }
}
