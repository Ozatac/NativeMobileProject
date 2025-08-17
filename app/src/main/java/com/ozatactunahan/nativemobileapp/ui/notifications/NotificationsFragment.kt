package com.ozatactunahan.nativemobileapp.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.ozatactunahan.nativemobileapp.databinding.FragmentNotificationsBinding
import com.ozatactunahan.nativemobileapp.ui.base.BaseFragment
import com.ozatactunahan.nativemobileapp.util.ErrorHandler
import com.ozatactunahan.nativemobileapp.util.NetworkUtils
import com.ozatactunahan.nativemobileapp.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding>(FragmentNotificationsBinding::inflate) {

    private val viewModel: NotificationsViewModel by viewModels()

    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        setupUI()
        observeData()
    }

    private lateinit var favoriteAdapter: FavoriteProductAdapter

    private fun setupUI() {
        setupRecyclerView()
        setupClearAllButton()
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteProductAdapter(
            onRemoveClick = { favorite ->
                viewModel.onUiEvent(NotificationsUiEvent.RemoveFromFavorites(favorite))
            },
            onProductClick = { favorite ->
                // TODO: Navigate to product detail
                // For now, just show a toast
                Toast.makeText(requireContext(), "Product: ${favorite.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerView.apply {
            adapter = favoriteAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }

        binding.retryButton.setOnClickListener {
            viewModel.onUiEvent(NotificationsUiEvent.Refresh)
        }
    }

    private fun setupClearAllButton() {
        binding.clearAllButton.setOnClickListener {
            viewModel.onUiEvent(NotificationsUiEvent.ClearAll)
        }
    }

    private fun observeData() {
        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            handleUiState(uiState)
        }

        collectLatestLifecycleFlow(viewModel.uiEffect) { effect ->
            handleUiEffect(effect)
        }
    }

    private fun handleUiState(uiState: NotificationsUiState) {
        binding.progressBar.isVisible = uiState.isLoading

        // Error handling
        if (!uiState.error.isNullOrBlank()) {
            binding.errorLayout.isVisible = true
            binding.errorText.text = uiState.error
            binding.recyclerView.isVisible = false
            binding.emptyStateText.isVisible = false
        } else {
            binding.errorLayout.isVisible = false
            
            // Favorites display
            if (uiState.favorites.isNotEmpty()) {
                binding.emptyStateText.isVisible = false
                binding.recyclerView.isVisible = true
                favoriteAdapter.submitList(uiState.favorites)
            } else {
                binding.emptyStateText.isVisible = true
                binding.recyclerView.isVisible = false
            }
        }
    }

    private fun handleUiEffect(effect: NotificationsUiEffect) {
        when (effect) {
            is NotificationsUiEffect.ShowToast -> {
                Toast.makeText(
                    requireContext(),
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is NotificationsUiEffect.ShowError -> {
                showError(effect.message)
            }

            is NotificationsUiEffect.AllFavoritesCleared -> {
                Toast.makeText(
                    requireContext(),
                    "All favorites cleared",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showError(message: String) {
        ErrorHandler.showError(
            context = requireContext(),
            errorLayout = binding.errorLayout,
            errorText = binding.errorText,
            retryButton = binding.retryButton,
            message = message,
            networkUtils = networkUtils
        ) {
            viewModel.onUiEvent(NotificationsUiEvent.Refresh)
        }
    }
}