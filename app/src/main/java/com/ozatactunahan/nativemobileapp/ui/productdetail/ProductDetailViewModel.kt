package com.ozatactunahan.nativemobileapp.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.data.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _favoriteState = MutableLiveData<Boolean>()
    val favoriteState: LiveData<Boolean> = _favoriteState

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            try {
                favoriteRepository.isFavorite(product.id).collect { isFavorite ->
                    if (isFavorite) {
                        favoriteRepository.removeFromFavorites(product.id)
                        _favoriteState.value = false
                    } else {
                        favoriteRepository.addToFavorites(product)
                        _favoriteState.value = true
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProductDetailViewModel", "Favori işlemi başarısız", e)
            }
        }
    }

    fun isFavorite(productId: String): LiveData<Boolean> {
        viewModelScope.launch {
            favoriteRepository.isFavorite(productId).collect { isFavorite ->
                _favoriteState.value = isFavorite
            }
        }
        return favoriteState
    }
}
