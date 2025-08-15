package com.ozatactunahan.nativemobileapp.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ozatactunahan.nativemobileapp.data.model.Order
import com.ozatactunahan.nativemobileapp.databinding.ItemOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersAdapter(
    private val onOrderClick: (Order) -> Unit,
    private val onDeleteOrder: (Order) -> Unit
) : ListAdapter<Order, OrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                orderNumberText.text = "Sipariş #${order.orderNumber}"
                orderDateText.text = formatDate(order.orderDate)
                totalAmountText.text = "$${String.format("%.2f", order.totalAmount)}"
                itemCountText.text = "${order.items.size} ürün"
                statusText.text = getStatusText(order.status)

                root.setOnClickListener {
                    onOrderClick(order)
                }

                deleteButton.setOnClickListener {
                    onDeleteOrder(order)
                }
            }
        }

        private fun formatDate(timestamp: Long): String {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            return formatter.format(date)
        }

        private fun getStatusText(status: com.ozatactunahan.nativemobileapp.data.model.OrderStatus): String {
            return when (status) {
                com.ozatactunahan.nativemobileapp.data.model.OrderStatus.PENDING -> "Beklemede"
                com.ozatactunahan.nativemobileapp.data.model.OrderStatus.CONFIRMED -> "Onaylandı"
                com.ozatactunahan.nativemobileapp.data.model.OrderStatus.SHIPPED -> "Kargoda"
                com.ozatactunahan.nativemobileapp.data.model.OrderStatus.DELIVERED -> "Teslim Edildi"
                com.ozatactunahan.nativemobileapp.data.model.OrderStatus.CANCELLED -> "İptal Edildi"
            }
        }
    }

    private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
