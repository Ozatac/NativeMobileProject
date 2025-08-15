package com.ozatactunahan.nativemobileapp.ui.productdetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.ui.base.BaseFragment
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.databinding.FragmentProductDetailBinding
import com.ozatactunahan.nativemobileapp.util.loadProductImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate) {

    private val viewModel: ProductDetailViewModel by viewModels()
    private var product: Product? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        arguments?.let { args ->
            product = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                args.getParcelable("product", Product::class.java)
            } else {
                @Suppress("DEPRECATION")
                args.getParcelable("product")
            }
            product?.let { setupUI(it) }
        }
        
        setupClickListeners()
        observeData()
        
        setupToolbar()
    }

    private fun setupUI(product: Product) {
        binding.apply {
            productDetailTitle.text = product.name
            productDetailDescription.text = product.description
            productDetailPrice.text = "$${product.price}"
            productDetailImage.loadProductImage(product.image)
            updateFavoriteIcon(product.id)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            productDetailFavoriteButton.setOnClickListener {
                product?.let { toggleFavorite(it) }
            }
            
            productDetailAddToCartButton.setOnClickListener {
                product?.let { addToCart(it) }
            }
        }
    }

    private fun toggleFavorite(product: Product) {
        viewModel.toggleFavorite(product)
    }

    private fun addToCart(product: Product) {
        viewModel.addToCart(product)
    }

    private fun updateFavoriteIcon(productId: String) {
        viewModel.isFavorite(productId).observe(viewLifecycleOwner) { isFavorite ->
            val favoriteIcon = if (isFavorite) {
                R.drawable.ic_favorite_filled
            } else {
                R.drawable.ic_favorite
            }
            binding.productDetailFavoriteButton.setImageResource(favoriteIcon)
        }
    }

    private fun observeData() {
        viewModel.cartState.observe(viewLifecycleOwner) { cartState ->
            when (cartState) {
                is ProductDetailViewModel.CartState.Loading -> {
                    binding.productDetailAddToCartButton.isEnabled = false
                    binding.productDetailAddToCartButton.text = "Ekleniyor..."
                }
                is ProductDetailViewModel.CartState.Success -> {
                    binding.productDetailAddToCartButton.isEnabled = true
                    binding.productDetailAddToCartButton.text = "Sepete Eklendi!"
                    Toast.makeText(requireContext(), cartState.message, Toast.LENGTH_SHORT).show()
                    
                    // 2 saniye sonra buton metnini geri al - lifecycle-aware yaklaşım
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(2000)
                        if (isAdded && !isDetached && view != null) {
                            try {
                                binding.productDetailAddToCartButton.text = "Sepete Ekle"
                            } catch (e: Exception) {
                                android.util.Log.d("ProductDetailFragment", "Binding null, buton metni güncellenmedi")
                            }
                        }
                    }
                }
                is ProductDetailViewModel.CartState.Error -> {
                    binding.productDetailAddToCartButton.isEnabled = true
                    binding.productDetailAddToCartButton.text = "Sepete Ekle"
                    Toast.makeText(requireContext(), cartState.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupToolbar() {
        activity?.let { activity ->
            if (activity is androidx.appcompat.app.AppCompatActivity) {
                activity.supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(true)
                    setDisplayShowHomeEnabled(true)
                    title = "Ürün Detayı"
                }
            }
        }
    }
}
