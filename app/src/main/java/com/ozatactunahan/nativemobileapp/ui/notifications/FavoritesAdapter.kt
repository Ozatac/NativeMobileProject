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

class FavoritesAdapter(
    private val onFavoriteClick: (FavoriteEntity) -> Unit,
    private val onProductClick: (FavoriteEntity) -> Unit
) : ListAdapter<FavoriteEntity, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {

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
                
                productImage.loadProductImage(favorite.imageUrl)
                
                favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
                favoriteButton.setOnClickListener {
                    onFavoriteClick(favorite)
                }
                
                root.setOnClickListener {
                    onProductClick(favorite)
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
