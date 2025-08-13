package com.ozatactunahan.nativemobileapp.data.remote

import com.ozatactunahan.nativemobileapp.data.model.Product
import retrofit2.http.GET

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>
}
