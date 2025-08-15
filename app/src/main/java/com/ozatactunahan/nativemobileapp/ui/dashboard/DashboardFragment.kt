package com.ozatactunahan.nativemobileapp.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.common.BaseFragment
import com.ozatactunahan.nativemobileapp.databinding.FragmentDashboardBinding
import com.ozatactunahan.nativemobileapp.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

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
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityIncrease = { cartItem ->
                viewModel.updateQuantity(
                    cartItem.productId,
                    cartItem.quantity + 1
                )
            },
            onQuantityDecrease = { cartItem ->
                viewModel.updateQuantity(
                    cartItem.productId,
                    cartItem.quantity - 1
                )
            },
            onRemoveItem = { cartItem ->
                viewModel.removeFromCart(cartItem.productId)
            }
        )

        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun setupClickListeners() {
        binding.clearCartButton.setOnClickListener {
            viewModel.clearCart()
        }

        binding.placeOrderButton.setOnClickListener {
            viewModel.placeOrder()
        }
    }

    private fun observeData() {
        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            handleUiState(uiState)
        }
    }

    private fun handleUiState(uiState: DashboardUiState) {
        binding.apply {
            if (!uiState.isLoading) {
                progressBar.visibility = View.GONE
            }

            uiState.error?.let { error ->
                errorText.text = error
                errorText.visibility = View.VISIBLE
            }

            // Sepet öğelerini göster
            cartAdapter.submitList(uiState.cartItems)

            // Toplam fiyatı göster
            totalPriceText.text = "Toplam: $${
                String.format(
                    "%.2f",
                    uiState.totalPrice
                )
            }"

            // Sipariş ver butonunu aktif/pasif yap
            placeOrderButton.isEnabled = uiState.cartItems.isNotEmpty()

            // Sepet boşsa mesaj göster
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

            // Sipariş başarılı mesajı
            if (uiState.orderPlaced) {
                showOrderSuccessDialog(uiState.orderNumber)
                // State'i reset'le ki tekrar gösterilmesin
                viewModel.resetOrderPlaced()
            }
        }
    }

    private fun showOrderSuccessDialog(orderNumber: String?) {
        val message = if (orderNumber != null) {
            "Siparişiniz başarıyla alındı! Sipariş numarası: $orderNumber"
        } else {
            "Siparişiniz başarıyla alındı!"
        }

        // Toast mesajı göster
        android.widget.Toast.makeText(
            requireContext(),
            message,
            android.widget.Toast.LENGTH_LONG
        ).show()

        // Kısa bir gecikme sonra profile ekranına git
        binding.root.postDelayed(
            {
                try {
                    // Navigation stack'i temizle ve Profile'a git
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
        ) // 2 saniye sonra git
    }
}