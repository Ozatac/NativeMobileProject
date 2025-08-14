package com.ozatactunahan.nativemobileapp.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.common.BaseFragment
import com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity
import com.ozatactunahan.nativemobileapp.databinding.FragmentNotificationsBinding
import com.ozatactunahan.nativemobileapp.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>(FragmentNotificationsBinding::inflate) {

    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(
            onFavoriteClick = { favorite ->
                viewModel.removeFromFavorites(favorite.productId)
            },
            onProductClick = { favorite ->
                navigateToProductDetail(favorite)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NotificationsFragment.adapter
        }
    }

    private fun observeData() {
        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            handleUiState(uiState)
        }
    }

    private fun handleUiState(uiState: NotificationsUiState) {
        binding.progressBar.isVisible = uiState.isLoading
        
        binding.errorText.run {
            isVisible = !uiState.error.isNullOrBlank()
            text = uiState.error.orEmpty()
        }
        
        if (uiState.favorites.isNotEmpty()) {
            binding.emptyStateText.isVisible = false
            binding.recyclerView.isVisible = true
            adapter.submitList(uiState.favorites)
        } else {
            binding.emptyStateText.isVisible = true
            binding.recyclerView.isVisible = false
        }
    }
    
    private fun navigateToProductDetail(favorite: FavoriteEntity) {
        val product = com.ozatactunahan.nativemobileapp.data.model.Product(
            id = favorite.productId,
            name = favorite.name,
            image = favorite.imageUrl,
            price = favorite.price,
            description = "Favori ürün açıklaması",
            model = "",
            brand = favorite.brand,
            createdAt = ""
        )
        
        // Navigation component ile ProductDetailFragment'a git
        val bundle = Bundle().apply {
            putParcelable("product", product)
        }
        findNavController().navigate(R.id.navigation_product_detail, bundle)
    }
}