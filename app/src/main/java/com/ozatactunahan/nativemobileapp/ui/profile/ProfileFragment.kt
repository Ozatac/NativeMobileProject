package com.ozatactunahan.nativemobileapp.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozatactunahan.nativemobileapp.databinding.FragmentProfileBinding
import com.ozatactunahan.nativemobileapp.ui.base.BaseFragment
import com.ozatactunahan.nativemobileapp.util.ErrorHandler
import com.ozatactunahan.nativemobileapp.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter
    
    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter(
            onOrderClick = { order ->
                viewModel.onUiEvent(ProfileUiEvent.OrderClick(order))
            },
            onDeleteOrder = { order ->
                viewModel.onUiEvent(ProfileUiEvent.DeleteOrder(order.id))
            }
        )

        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ordersAdapter
        }
        
        // Retry button click listener
        binding.retryButton.setOnClickListener {
            viewModel.onUiEvent(ProfileUiEvent.Refresh)
        }
    }

        private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                ordersAdapter.submitList(uiState.orders)
                updateEmptyState(uiState.orders.isEmpty())

                binding.progressBar.visibility = if (uiState.isLoading) View.VISIBLE else View.GONE
            }
        }
        
        // UI Effect'leri dinle
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiEffect.collect { effect ->
                handleUiEffect(effect)
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyOrdersLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.ordersRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun handleUiEffect(effect: ProfileUiEffect) {
        when (effect) {
            is ProfileUiEffect.ShowToast -> {
                Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
            }
            is ProfileUiEffect.ShowError -> {
                showError(effect.message)
            }
            is ProfileUiEffect.NavigateToOrderDetail -> {
                // TODO: Navigate to order detail
                Toast.makeText(requireContext(), "Order details: ${effect.order.orderNumber}", Toast.LENGTH_SHORT).show()
            }
            is ProfileUiEffect.OrderDeletedSuccess -> {
                Toast.makeText(requireContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show()
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
            viewModel.onUiEvent(ProfileUiEvent.Refresh)
        }
    }
}
