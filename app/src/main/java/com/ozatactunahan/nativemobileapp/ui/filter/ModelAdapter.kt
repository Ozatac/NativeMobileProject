package com.ozatactunahan.nativemobileapp.ui.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ozatactunahan.nativemobileapp.databinding.ItemModelBinding

class ModelAdapter(
    private val onModelToggle: (FilterItem, Boolean) -> Unit
) : ListAdapter<FilterItem, ModelAdapter.ModelViewHolder>(ModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val binding = ItemModelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ModelViewHolder(
        private val binding: ItemModelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: FilterItem) {
            binding.apply {
                modelText.text = model.name
                modelCheckBox.isChecked = model.isSelected

                modelCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    onModelToggle(model, isChecked)
                }

                root.setOnClickListener {
                    val newCheckedState = !modelCheckBox.isChecked
                    modelCheckBox.isChecked = newCheckedState
                    onModelToggle(model, newCheckedState)
                }
            }
        }
    }

    private class ModelDiffCallback : DiffUtil.ItemCallback<FilterItem>() {
        override fun areItemsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
            return oldItem == newItem
        }
    }
}
