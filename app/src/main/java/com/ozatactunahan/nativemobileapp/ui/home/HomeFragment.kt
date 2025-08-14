package com.ozatactunahan.nativemobileapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.common.BaseFragment
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.databinding.FragmentHomeBinding
import com.ozatactunahan.nativemobileapp.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by activityViewModels()
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
        setupFilterButtons()
    }

    private fun setupRecyclerView() {
        adapter = ProductPagingAdapter(
            onProductClick = ::navigateToProductDetail,
            onAddToCartClick = ::addToCart,
            onFavoriteClick = ::toggleFavorite
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = this@HomeFragment.adapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    handleUiState(uiState)
                    adapter.submitData(lifecycle, uiState.pagingData)

                }
            }
        }

        collectLatestLifecycleFlow(adapter.loadStateFlow) { loadStates ->
            handleLoadStates(loadStates)
        }

        observeFavoriteStates()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchProducts(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                when {
                    newText.isNullOrBlank() -> {
                        viewModel.clearSearch()
                    }

                    newText.length >= 3 -> {
                        viewModel.searchProducts(newText)
                    }
                }
                return true
            }
        })
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

        if (uiState.isFiltered) {
            binding.filterButton.text = "Filtreler Aktif (${uiState.filteredProducts.size})"
        } else {
            binding.filterButton.text = "Filtrele ve Sırala"
        }
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
        val bundle = Bundle().apply {
            putParcelable("product", product)
        }
        findNavController().navigate(R.id.navigation_product_detail, bundle)
    }

    private fun addToCart(product: Product) {
        // TODO: Add to cart functionality
    }

    private fun toggleFavorite(product: Product, isFavorite: Boolean) {
        viewModel.toggleFavorite(product)
    }

    private fun observeFavoriteStates() {
        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            uiState.favoriteStates.forEach { (productId, isFavorite) ->
                adapter.updateFavoriteState(productId, isFavorite)
            }
        }
    }

    private fun setupFilterButtons() {
        binding.filterButton.setOnClickListener {
            navigateToFilter()
        }

        binding.filterButton.setOnLongClickListener {
            viewModel.clearFilters()
            true
        }
    }

    private fun navigateToFilter() {
        findNavController().navigate(R.id.filter_fragment)
    }
}