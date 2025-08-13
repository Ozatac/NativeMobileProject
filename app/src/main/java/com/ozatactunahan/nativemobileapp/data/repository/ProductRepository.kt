package com.ozatactunahan.nativemobileapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.data.paging.ProductPagingSource
import com.ozatactunahan.nativemobileapp.data.remote.ProductApiService
import com.ozatactunahan.nativemobileapp.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService
) : ProductRepository {

    override fun getProducts(scope: CoroutineScope): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                ProductPagingSource(apiService)
            }
        ).flow.cachedIn(scope) // ✅ Scope'u parametre olarak alıyoruz
    }
}