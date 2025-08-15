package com.ozatactunahan.nativemobileapp.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.databinding.ItemCartProductBinding

class CartAdapter(
    private val onQuantityIncrease: (CartItem) -> Unit,
    private val onQuantityDecrease: (CartItem) -> Unit,
    private val onRemoveItem: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                productName.text = cartItem.productName
                productBrand.text = cartItem.productBrand
                productPrice.text = "$${cartItem.productPrice}"
                quantityText.text = cartItem.quantity.toString()

                // Resim yükleme
                Glide.with(productImage.context)
                    .load(cartItem.productImage)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                    .into(productImage)

                // Quantity artırma/azaltma
                increaseButton.setOnClickListener {
                    onQuantityIncrease(cartItem)
                }

                decreaseButton.setOnClickListener {
                    if (cartItem.quantity > 1) {
                        onQuantityDecrease(cartItem)
                    }
                }

                // Ürünü kaldırma
                removeButton.setOnClickListener {
                    onRemoveItem(cartItem)
                }

                // Quantity 1 ise decrease butonunu devre dışı bırak
                decreaseButton.isEnabled = cartItem.quantity > 1
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
