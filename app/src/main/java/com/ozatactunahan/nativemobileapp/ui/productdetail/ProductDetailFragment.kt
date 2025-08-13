package com.ozatactunahan.nativemobileapp.ui.productdetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.common.BaseFragment
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.databinding.FragmentProductDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(FragmentProductDetailBinding::inflate) {

    private val viewModel: ProductDetailViewModel by viewModels()
    private var product: Product? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Arguments'dan ürün bilgisini al
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
        
        // Toolbar'da geri butonunu aktif et
        setupToolbar()
    }

    private fun setupUI(product: Product) {
        binding.apply {
            // Ürün adı
            productDetailTitle.text = product.name
            
            // Ürün açıklaması
            productDetailDescription.text = product.description
            
            // Fiyat
            productDetailPrice.text = "$${product.price}"
            
            // Ürün görseli
            Glide.with(productDetailImage.context)
                .load(product.image)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(productDetailImage)
            
            // Favori durumunu göster
            updateFavoriteIcon(product.id)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // Favori butonu
            productDetailFavoriteButton.setOnClickListener {
                product?.let { toggleFavorite(it) }
            }
            
            // Sepete ekle butonu
            productDetailAddToCartButton.setOnClickListener {
                product?.let { addToCart(it) }
            }
        }
    }

    private fun toggleFavorite(product: Product) {
        viewModel.toggleFavorite(product)
    }

    private fun addToCart(product: Product) {
        // TODO: Sepete ekleme işlemi
        android.util.Log.d("ProductDetail", "Sepete eklendi: ${product.name}")
    }

    private fun updateFavoriteIcon(productId: String) {
        // TODO: Favori durumunu ViewModel'den al ve ikonu güncelle
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
        // TODO: ViewModel'den veri observe et
    }

    private fun setupToolbar() {
        // Activity'de toolbar varsa geri butonu ekle
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
