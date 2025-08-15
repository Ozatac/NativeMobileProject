package com.ozatactunahan.nativemobileapp.ui.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozatactunahan.nativemobileapp.ui.base.BaseFragment
import com.ozatactunahan.nativemobileapp.databinding.FragmentFilterBinding
import com.ozatactunahan.nativemobileapp.ui.home.HomeViewModel
import com.ozatactunahan.nativemobileapp.util.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : BaseFragment<FragmentFilterBinding>(FragmentFilterBinding::inflate) {

    private val viewModel: FilterViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var sortAdapter: SortByAdapter
    private lateinit var brandAdapter: BrandAdapter
    private lateinit var modelAdapter: ModelAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeData()
    }

    private fun setupUI() {
        setupRecyclerViews()
        setupClickListeners()
    }

    private fun setupRecyclerViews() {
        sortAdapter = SortByAdapter { sortOption ->
            viewModel.setSelectedSortOption(sortOption)
        }
        binding.sortByRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sortAdapter
        }

        brandAdapter = BrandAdapter { brand, isChecked ->
            viewModel.toggleBrandSelection(brand, isChecked)
        }
        binding.brandRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = brandAdapter
        }

        modelAdapter = ModelAdapter { model, isChecked ->
            viewModel.toggleModelSelection(model, isChecked)
        }
        binding.modelRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = modelAdapter
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            applyFiltersButton.setOnClickListener {
                val filterResult = viewModel.applyFilters()
                homeViewModel.applyFilters(filterResult)
                findNavController().navigateUp()
            }

            brandSearchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.searchBrands(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { viewModel.searchBrands(it) }
                    return true
                }
            })

            modelSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.searchModels(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { viewModel.searchModels(it) }
                    return true
                }
            })
        }
    }

    private fun observeData() {
        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->
            handleUiState(uiState)
        }
    }

    private fun handleUiState(uiState: FilterUiState) {
        sortAdapter.submitList(uiState.sortOptions)

        brandAdapter.submitList(uiState.filteredBrands)

        modelAdapter.submitList(uiState.filteredModels)
    }
}
