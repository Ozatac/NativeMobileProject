package com.ozatactunahan.nativemobileapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ozatactunahan.nativemobileapp.databinding.ItemLoadingFooterBinding

class LoadingFooterAdapter : LoadStateAdapter<LoadingFooterAdapter.LoadingFooterViewHolder>() {

    override fun onBindViewHolder(holder: LoadingFooterViewHolder, loadState: LoadState) {
        // LoadState'e göre UI'ı güncelle
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingFooterViewHolder {
        val binding = ItemLoadingFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadingFooterViewHolder(binding)
    }

    class LoadingFooterViewHolder(
        private val binding: ItemLoadingFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.Loading -> {
                    binding.root.visibility = View.VISIBLE
                }
                is LoadState.Error -> {
                    binding.root.visibility = View.GONE
                }
                is LoadState.NotLoading -> {
                    binding.root.visibility = View.GONE
                }
            }
        }
    }
}
