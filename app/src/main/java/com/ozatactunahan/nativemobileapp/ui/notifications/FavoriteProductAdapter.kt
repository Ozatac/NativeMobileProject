package com.ozatactunahan.nativemobileapp.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity
import com.ozatactunahan.nativemobileapp.databinding.ItemFavoriteProductBinding
import com.ozatactunahan.nativemobileapp.util.loadProductImage

class FavoriteProductAdapter(
    private val onRemoveClick: (FavoriteEntity) -> Unit,
    private val onProductClick: (FavoriteEntity) -> Unit
) : ListAdapter<FavoriteEntity, FavoriteProductAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(
        private val binding: ItemFavoriteProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: FavoriteEntity) {
            binding.apply {
                productName.text = favorite.name
                productBrand.text = favorite.brand
                productPrice.text = "$${favorite.price}"

                // Ürün resmini yükle
                productImage.loadProductImage(favorite.imageUrl)

                // Ürün tıklama
                root.setOnClickListener {
                    onProductClick(favorite)
                }

                // Favoriden çıkarma butonu
                removeButton.setOnClickListener {
                    onRemoveClick(favorite)
                }
            }
        }
    }

    private class FavoriteDiffCallback : DiffUtil.ItemCallback<FavoriteEntity>() {
        override fun areItemsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
            return oldItem == newItem
        }
    }
}
