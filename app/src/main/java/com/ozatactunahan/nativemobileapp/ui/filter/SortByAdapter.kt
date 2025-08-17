package com.ozatactunahan.nativemobileapp.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ozatactunahan.nativemobileapp.databinding.ItemSortByBinding

class SortByAdapter(
    private val onSortOptionClick: (SortOption) -> Unit
) : ListAdapter<SortOption, SortByAdapter.SortByViewHolder>(SortByDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SortByViewHolder {
        val binding = ItemSortByBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SortByViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SortByViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class SortByViewHolder(
        private val binding: ItemSortByBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sortOption: SortOption) {
            binding.apply {
                sortByText.text = sortOption.name
                sortByRadioButton.isChecked = sortOption.isSelected

                sortByRadioButton.setOnClickListener {
                    onSortOptionClick(sortOption)
                }

                root.setOnClickListener {
                    onSortOptionClick(sortOption)
                }
            }
        }
    }

    private class SortByDiffCallback : DiffUtil.ItemCallback<SortOption>() {
        override fun areItemsTheSame(
            oldItem: SortOption,
            newItem: SortOption
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SortOption,
            newItem: SortOption
        ): Boolean {
            return oldItem == newItem
        }
    }
}
