package com.ozatactunahan.nativemobileapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.data.paging.ProductPagingSource
import com.ozatactunahan.nativemobileapp.data.remote.ProductApiService
import com.ozatactunahan.nativemobileapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService
) : ProductRepository {

    override fun getProducts(searchQuery: String?): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                ProductPagingSource(
                    apiService,
                    searchQuery
                )
            }).flow
    }

    suspend fun getProductsSync(): List<Product> {
        return try {
            apiService.getProducts()
        } catch (e: Exception) {
            android.util.Log.e("ProductRepository", "Ürünler alınamadı", e)
            emptyList()
        }
    }
}