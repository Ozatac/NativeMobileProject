package com.ozatactunahan.nativemobileapp.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ozatactunahan.nativemobileapp.databinding.ItemBrandBinding

class BrandAdapter(
    private val onBrandToggle: (FilterItem, Boolean) -> Unit
) : ListAdapter<FilterItem, BrandAdapter.BrandViewHolder>(BrandDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding = ItemBrandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BrandViewHolder(
        private val binding: ItemBrandBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: FilterItem) {
            binding.apply {
                brandText.text = brand.name
                brandCheckBox.isChecked = brand.isSelected

                brandCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    onBrandToggle(brand, isChecked)
                }

                root.setOnClickListener {
                    val newCheckedState = !brandCheckBox.isChecked
                    brandCheckBox.isChecked = newCheckedState
                    onBrandToggle(brand, newCheckedState)
                }
            }
        }
    }

    private class BrandDiffCallback : DiffUtil.ItemCallback<FilterItem>() {
        override fun areItemsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
            return oldItem == newItem
        }
    }
}
