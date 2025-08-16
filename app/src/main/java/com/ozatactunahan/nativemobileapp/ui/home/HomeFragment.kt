package com.ozatactunahan.nativemobileapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ozatactunahan.nativemobileapp.R
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.databinding.FragmentHomeBinding
import com.ozatactunahan.nativemobileapp.ui.base.BaseFragment
import com.ozatactunahan.nativemobileapp.util.ErrorHandler
import com.ozatactunahan.nativemobileapp.util.NetworkUtils
import com.ozatactunahan.nativemobileapp.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: ProductPagingAdapter
    
    @Inject
    lateinit var networkUtils: NetworkUtils

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupSearchView()
        setupFilterButtons()
        setupRetryButton()
    }

    private fun setupRecyclerView() {
        adapter = ProductPagingAdapter(
            onProductClick = { product -> viewModel.onUiEvent(HomeUiEvent.ProductClick(product)) },
            onAddToCartClick = { product -> viewModel.onUiEvent(HomeUiEvent.AddToCartClick(product)) },
            onFavoriteClick = { product, isFavorite -> viewModel.onUiEvent(HomeUiEvent.FavoriteClick(product)) }
        )

        val loadingFooterAdapter = LoadingFooterAdapter()
        
        val concatAdapter = androidx.recyclerview.widget.ConcatAdapter(adapter, loadingFooterAdapter)
        
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = concatAdapter
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
        
        collectLatestLifecycleFlow(viewModel.uiEffect) { effect ->
            handleUiEffect(effect)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.onUiEvent(HomeUiEvent.SearchSubmit(it))
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onUiEvent(HomeUiEvent.SearchQuery(newText.orEmpty()))
                return true
            }
        })
    }

    private fun handleLoadStates(loadStates: CombinedLoadStates) = with(loadStates) {
        val isRefreshLoading = refresh is LoadState.Loading
        val isAppendLoading = append is LoadState.Loading

        showLoading(isRefreshLoading)
        
        (refresh as? LoadState.Error)?.let {
            showError(it.error.message)
        }
    }

    private fun handleUiState(uiState: HomeUiState) {
        showLoading(uiState.isLoading)
        uiState.error?.let(::showError)
    }

    private fun handleUiEffect(effect: HomeUiEffect) {
        when (effect) {
            is HomeUiEffect.NavigateToProductDetail -> {
                navigateToProductDetail(effect.product)
            }
            is HomeUiEffect.NavigateToFilter -> {
                navigateToFilter()
            }
            is HomeUiEffect.ShowToast -> {
                Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
            }
            is HomeUiEffect.ShowSnackbar -> {
                Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
            }
            is HomeUiEffect.ShowError -> {
                showError(effect.message)
            }
            is HomeUiEffect.ClearSearchView -> {
                binding.searchView.setQuery("", false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.apply {
            progressBar.isVisible = show
        }
    }

    private fun showPagingLoading(show: Boolean) {
    }

    private fun showError(message: String?) {
        ErrorHandler.showError(
            context = requireContext(),
            errorLayout = binding.errorLayout,
            errorText = binding.errorText,
            retryButton = binding.retryButton,
            message = message,
            networkUtils = networkUtils
        ) {
            viewModel.onUiEvent(HomeUiEvent.Refresh)
        }
    }

    private fun navigateToProductDetail(product: Product) {
        val bundle = Bundle().apply {
            putParcelable("product", product)
        }
        findNavController().navigate(R.id.navigation_product_detail, bundle)
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
            viewModel.onUiEvent(HomeUiEvent.FilterButtonClick)
        }

        binding.filterButton.setOnLongClickListener {
            viewModel.onUiEvent(HomeUiEvent.FilterButtonLongClick)
            true
        }
    }

    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            viewModel.onUiEvent(HomeUiEvent.Refresh)
        }
    }
    


    private fun navigateToFilter() {
        findNavController().navigate(R.id.filter_fragment)
    }
}