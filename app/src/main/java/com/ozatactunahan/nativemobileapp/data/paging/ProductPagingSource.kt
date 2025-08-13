package com.ozatactunahan.nativemobileapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.data.remote.ProductApiService

class ProductPagingSource(
    private val apiService: ProductApiService,
    private val searchQuery: String? = null,
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            
            // API'den tüm ürünleri çekiyoruz (pagination desteklemiyor)
            val response = apiService.getProducts()

            // Arama varsa filtreleme yap
            val filteredProducts = if (!searchQuery.isNullOrBlank()) {
                response.filter { product ->
                    product.name.contains(searchQuery, ignoreCase = true) ||
                            product.description?.contains(searchQuery, ignoreCase = true) == true
                }
            } else {
                response
            }

            // Manuel pagination yapıyoruz
            val startIndex = (page - 1) * pageSize
            val endIndex = minOf(startIndex + pageSize, filteredProducts.size)

            val products = if (startIndex < filteredProducts.size) {
                filteredProducts.subList(startIndex, endIndex)
            } else {
                emptyList()
            }

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endIndex >= filteredProducts.size) null else page + 1

            LoadResult.Page(
                data = products,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
