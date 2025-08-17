package com.ozatactunahan.nativemobileapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.databinding.ItemProductBinding
import com.ozatactunahan.nativemobileapp.util.loadProductImage

class ProductPagingAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit,
    private val onFavoriteClick: (Product, Boolean) -> Unit
) : PagingDataAdapter<Product, ProductPagingAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private val favoriteStates = mutableMapOf<String, Boolean>()
    private val viewHolderMap = mutableMapOf<String, ProductViewHolder>()

    fun updateFavoriteState(
        productId: String,
        isFavorite: Boolean
    ) {
        favoriteStates[productId] = isFavorite
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
            viewHolderMap[it.id] = holder
        }
    }

    override fun onViewDetachedFromWindow(holder: ProductViewHolder) {
        super.onViewDetachedFromWindow(holder)
        viewHolderMap.entries.removeAll { it.value == holder }
    }

    override fun onViewRecycled(holder: ProductViewHolder) {
        super.onViewRecycled(holder)
        viewHolderMap.entries.removeAll { it.value == holder }
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentProduct: Product? = null

        fun bind(product: Product) {
            currentProduct = product

            binding.apply {
                productName.text = product.name
                productBrand.text = product.brand
                productPrice.text = "$${product.price}"

                productImage.loadProductImage(product.image)

                root.setOnClickListener {
                    onProductClick(product)
                }

                addToCartButton.setOnClickListener {
                    onAddToCartClick(product)
                }

                updateFavoriteIcon(product.id)

                favoriteButton.setOnClickListener {
                    onFavoriteClick(product, favoriteStates[product.id] ?: false)
                }
            }
        }

        fun updateFavoriteIcon(productId: String) {
            if (currentProduct?.id == productId) {
                val isFavorite = favoriteStates[productId] ?: false
                val favoriteIcon = if (isFavorite) {
                    R.drawable.ic_favorite_filled
                } else {
                    R.drawable.ic_favorite_border
                }
                binding.favoriteButton.setImageResource(favoriteIcon)
            }
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

        override fun getChangePayload(oldItem: Product, newItem: Product): Any? {
            return if (oldItem.id == newItem.id && oldItem != newItem) {
                "UPDATE"
            } else {
                null
            }
        }
    }
}