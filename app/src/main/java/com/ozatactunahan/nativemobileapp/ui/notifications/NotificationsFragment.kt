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

    private fun setupUI() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.retryButton.setOnClickListener {
            viewModel.onUiEvent(NotificationsUiEvent.Refresh)
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

        binding.errorText.run {
            isVisible = !uiState.error.isNullOrBlank()
            text = uiState.error.orEmpty()
        }

        if (uiState.notifications.isNotEmpty()) {
            binding.emptyStateText.isVisible = false
            binding.recyclerView.isVisible = true
        } else {
            binding.emptyStateText.isVisible = true
            binding.recyclerView.isVisible = false
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

            is NotificationsUiEffect.AllNotificationsCleared -> {
                Toast.makeText(
                    requireContext(),
                    "All notifications cleared",
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