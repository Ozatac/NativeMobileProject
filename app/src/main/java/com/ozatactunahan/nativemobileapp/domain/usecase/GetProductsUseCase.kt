package com.ozatactunahan.nativemobileapp.domain.usecase

import androidx.paging.PagingData
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(searchQuery: String? = null): Flow<PagingData<Product>> {
        return repository.getProducts(searchQuery)
    }
}