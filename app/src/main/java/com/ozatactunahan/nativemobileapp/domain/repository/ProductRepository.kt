package com.ozatactunahan.nativemobileapp.domain.repository

import androidx.paging.PagingData
import com.ozatactunahan.nativemobileapp.data.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(searchQuery: String? = null): Flow<PagingData<Product>>
}