package com.ozatactunahan.nativemobileapp.ui.home

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.databinding.ItemProductBinding

class ProductPagingAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit,
    private val onFavoriteClick: (Product, Boolean) -> Unit
) : PagingDataAdapter<Product, ProductPagingAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private val favoriteStates = mutableMapOf<String, Boolean>()
    private val viewHolderMap = mutableMapOf<String, ProductViewHolder>()

    fun updateFavoriteState(productId: String, isFavorite: Boolean) {
        favoriteStates[productId] = isFavorite
        
        // ViewHolder'da güncellemeyi dene
        viewHolderMap[productId]?.updateFavoriteIcon(productId)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product = getItem(position)
        product?.let { 
            holder.bind(it)
            // ViewHolder'ı kaydet
            viewHolderMap[it.id] = holder
        }
    }

    override fun onViewDetachedFromWindow(holder: ProductViewHolder) {
        super.onViewDetachedFromWindow(holder)
        // ViewHolder'ı kaldır
        viewHolderMap.values.remove(holder)
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(product: Product) {
            binding.apply {
                productName.text = product.name
                productBrand.text = product.brand
                productPrice.text = "$${product.price}"

                try {
                    Glide.with(productImage.context)
                        .load(product.image)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .fallback(R.drawable.placeholder_image)
                        .timeout(15000)
                        .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                            override fun onLoadFailed(
                                p0: GlideException?,
                                p1: Any?,
                                p2: Target<Drawable?>,
                                p3: Boolean
                            ): Boolean {
                                Log.w("Glide", "Failed to load image: ${product.image}")
                                return false
                            }

                            override fun onResourceReady(
                                p0: Drawable,
                                p1: Any,
                                p2: Target<Drawable?>?,
                                p3: DataSource,
                                p4: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(productImage)
                } catch (e: Exception) {
                    productImage.setImageResource(R.drawable.placeholder_image)
                    android.util.Log.e("Glide", "Exception loading image: ${product.image}", e)
                }

                root.setOnClickListener {
                    onProductClick(product)
                }

                addToCartButton.setOnClickListener {
                    onAddToCartClick(product)
                }

                // Favori durumunu göster
                updateFavoriteIcon(product.id)

                favoriteButton.setOnClickListener {
                    onFavoriteClick(product, !(favoriteStates[product.id] ?: false))
                }
            }
        }

        fun updateFavoriteIcon(productId: String) {
                            val isFavorite = favoriteStates[productId] ?: false
                val favoriteIcon = if (isFavorite) {
                    R.drawable.ic_favorite_filled
                } else {
                    R.drawable.ic_favorite
                }
            binding.favoriteButton.setImageResource(favoriteIcon)
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }
    }
}
