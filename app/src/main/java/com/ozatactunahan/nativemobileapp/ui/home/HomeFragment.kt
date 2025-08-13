package com.ozatactunahan.nativemobileapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.databinding.FragmentHomeBinding
import com.ozatactunahan.nativemobileapp.common.BaseFragment
import com.ozatactunahan.nativemobileapp.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductPagingAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        adapter = ProductPagingAdapter(
            onProductClick = ::navigateToProductDetail,
            onAddToCartClick = ::addToCart
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(
                context,
                2
            )
            adapter = this@HomeFragment.adapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?) = true
        })
    }

    private fun observeData() {
        // PagingData observe
        collectLatestLifecycleFlow(
            viewModel.uiState.map { it.pagingData }.distinctUntilChanged()
        ) { pagingData ->
            adapter.submitData(
                lifecycle,
                pagingData
            )
        }

        // LoadStates observe
        collectLatestLifecycleFlow(adapter.loadStateFlow) { loadStates ->
            handleLoadStates(loadStates)
        }

        // UiState observe
        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            handleUiState(uiState)
        }
    }

    private fun handleLoadStates(loadStates: CombinedLoadStates) = with(loadStates) {
        val isRefreshLoading = refresh is LoadState.Loading
        val isAppendLoading = append is LoadState.Loading

        showLoading(isRefreshLoading)
        showPagingLoading(isAppendLoading)

        (refresh as? LoadState.Error)?.let {
            showError(it.error.message)
        }
    }

    private fun handleUiState(uiState: HomeUiState) {
        if (!uiState.isLoading) showLoading(false)
        uiState.error?.let(::showError)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.isVisible = show
    }

    private fun showPagingLoading(show: Boolean) {
        binding.progressBar.isVisible = show
    }

    private fun showError(message: String?) {
        binding.errorText.run {
            isVisible = !message.isNullOrBlank()
            text = message.orEmpty()
        }
    }

    private fun navigateToProductDetail(product: Product) {
        print("Product detail navigation")
        // TODO: Navigate to product detail
    }

    private fun addToCart(product: Product) {
        print("Product detail navigation")
        // TODO: Add to cart functionality
    }
}