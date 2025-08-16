package com.ozatactunahan.nativemobileapp.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.databinding.FragmentDashboardBinding
import com.ozatactunahan.nativemobileapp.ui.base.BaseFragment
import com.ozatactunahan.nativemobileapp.util.ErrorHandler
import com.ozatactunahan.nativemobileapp.util.NetworkUtils
import com.ozatactunahan.nativemobileapp.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    
    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityIncrease = { cartItem ->
                viewModel.onUiEvent(
                    DashboardUiEvent.UpdateQuantity(
                        cartItem.productId,
                        cartItem.quantity + 1
                    )
                )
            },
            onQuantityDecrease = { cartItem ->
                viewModel.onUiEvent(
                    DashboardUiEvent.UpdateQuantity(
                        cartItem.productId,
                        cartItem.quantity - 1
                    )
                )
            },
            onRemoveItem = { cartItem ->
                viewModel.onUiEvent(DashboardUiEvent.RemoveItem(cartItem.productId))
            },
            onProductClick = { cartItem ->
                navigateToProductDetail(cartItem)
            }
        )

        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun setupClickListeners() {
        binding.clearCartButton.setOnClickListener {
            viewModel.onUiEvent(DashboardUiEvent.ClearCart)
        }

        binding.placeOrderButton.setOnClickListener {
            viewModel.onUiEvent(DashboardUiEvent.PlaceOrder)
        }
        
        binding.retryButton.setOnClickListener {
            viewModel.onUiEvent(DashboardUiEvent.Refresh)
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

    private fun handleUiState(uiState: DashboardUiState) {
        binding.apply {
            if (!uiState.isLoading) {
                progressBar.visibility = View.GONE
            }

                         uiState.error?.let { error ->
                 showError(error)
             }

            cartAdapter.submitList(uiState.cartItems)

            totalPriceText.text = "Total: $${
                String.format(
                    "%.2f",
                    uiState.totalPrice
                )
            }"

            placeOrderButton.isEnabled = uiState.cartItems.isNotEmpty()
            
            if (uiState.cartItems.isNotEmpty()) {
                placeOrderButton.alpha = 1.0f
                placeOrderButton.cardElevation = 8f
            } else {
                placeOrderButton.alpha = 0.6f
                placeOrderButton.cardElevation = 2f
            }

            if (uiState.cartItems.isEmpty()) {
                emptyCartMessage.visibility = View.VISIBLE
                cartRecyclerView.visibility = View.GONE
                totalPriceText.visibility = View.GONE
                placeOrderButton.visibility = View.GONE
                clearCartButton.visibility = View.GONE
            } else {
                emptyCartMessage.visibility = View.GONE
                cartRecyclerView.visibility = View.VISIBLE
                totalPriceText.visibility = View.VISIBLE
                placeOrderButton.visibility = View.VISIBLE
                clearCartButton.visibility = View.VISIBLE
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
            viewModel.onUiEvent(DashboardUiEvent.Refresh)
        }
    }

    private fun handleUiEffect(effect: DashboardUiEffect) {
        when (effect) {
            is DashboardUiEffect.ShowOrderSuccess -> {
                showOrderSuccessDialog(effect.orderNumber)
            }

            is DashboardUiEffect.ShowError -> {
                showError(effect.message)
            }

            is DashboardUiEffect.ShowToast -> {
                Toast.makeText(
                    requireContext(),
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is DashboardUiEffect.NavigateToProfile -> {
                try {
                    findNavController().navigate(R.id.navigation_profile)
                } catch (e: Exception) {
                    android.util.Log.e(
                        "DashboardFragment",
                        "Navigation error",
                        e
                    )
                }
            }

            is DashboardUiEffect.ClearCartSuccess -> {
                Toast.makeText(
                    requireContext(),
                    "Cart cleared successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToProductDetail(cartItem: CartItem) {
        val product = Product(
            id = cartItem.productId,
            name = cartItem.productName,
            brand = cartItem.productBrand,
            model = "",
            price = cartItem.productPrice,
            image = cartItem.productImage,
            createdAt = "",
            description = ""
        )
        
        val bundle = Bundle().apply {
            putParcelable("product", product)
        }
        
        try {
            findNavController().navigate(R.id.navigation_product_detail, bundle)
        } catch (e: Exception) {
            android.util.Log.e(
                "DashboardFragment",
                "Navigation to product detail error",
                e
            )
        }
    }

    private fun showOrderSuccessDialog(orderNumber: String?) {
        val message = if (orderNumber != null) {
            "Your order has been received successfully! Order number: $orderNumber"
        } else {
            "Your order has been received successfully!"
        }

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()

        binding.root.postDelayed(
            {
                try {
                    val navController = findNavController()
                    if (navController.currentDestination?.id == R.id.navigation_dashboard) {
                        navController.navigate(R.id.navigation_profile) {
                            popUpTo(R.id.navigation_dashboard) { inclusive = true }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(
                        "DashboardFragment",
                        "Navigation error",
                        e
                    )
                }
            },
            2000
        )
    }
}