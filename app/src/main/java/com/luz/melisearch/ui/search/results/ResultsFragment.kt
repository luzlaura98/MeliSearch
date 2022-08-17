package com.luz.melisearch.ui.search.results

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.luz.melisearch.R
import com.luz.melisearch.domain.entities.ItemMeli
import com.luz.melisearch.databinding.FragmentResultsBinding
import com.luz.melisearch.domain.repo.ItemsRepository
import com.luz.melisearch.ui.adapters.ItemsLoadStateAdapter
import com.luz.melisearch.ui.adapters.ResultsAdapter
import com.luz.melisearch.ui.base.BaseFragment
import com.luz.melisearch.ui.itemDetail.ItemDetailActivity
import com.luz.melisearch.utils.PlaceholderManager
import com.luz.melisearch.utils.isFirstItem
import com.luz.melisearch.utils.parseError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Luz on 15/8/2022.
 */
@AndroidEntryPoint
class ResultsFragment : BaseFragment() {
    companion object {
        const val EXTRA_KEYWORD = "keyword"

        @JvmStatic
        fun newInstance(keyword: String): ResultsFragment {
            val fragment = ResultsFragment()
            fragment.arguments = bundleOf(EXTRA_KEYWORD to keyword)
            return fragment
        }
    }

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    private val placeholderManager by lazy {
        PlaceholderManager(
            binding.placeholderContainer,
            binding.recyclerView
        )
    }

    private val adapter: ResultsAdapter by lazy { ResultsAdapter(this::onClickItemMeli) }

    private val viewModel: ResultsViewModel by viewModels()
    @Inject
    lateinit var repository: ItemsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val extraKeyword = arguments?.getString(EXTRA_KEYWORD)
        if (extraKeyword == null){
            showSnackErrorMessage()
            return
        }
        viewModel.keyword = extraKeyword
        initRecyclerView()
        initAdapter()
    }

    private fun initRecyclerView() {
        val marginVertical = resources.getDimensionPixelOffset(R.dimen.dimen_medium_0_size)
        val marginHorizontal = resources.getDimensionPixelOffset(R.dimen.margin_app)
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                with(outRect) {
                    left = marginHorizontal
                    right = marginHorizontal
                    top = if (parent.isFirstItem(view)) marginVertical else marginVertical / 2
                    bottom = marginVertical
                }
            }
        })
    }

    private fun initAdapter() {
        binding.recyclerView.adapter = adapter.withLoadStateFooter(ItemsLoadStateAdapter(adapter))

        // Submit pages to adapter
        lifecycleScope.launch {
            viewModel.myFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        // Update screen for the first page
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .collect { loadStates ->
                    when (loadStates.refresh) {
                        is LoadState.Loading -> placeholderManager.showLoading(true)
                        is LoadState.Error -> {
                            // When LoadState is Error, it will not pass for NotLoading
                            val error = (loadStates.refresh as LoadState.Error).error
                            val errorMessage = error.parseError(requireContext())
                            placeholderManager.showPlaceholderMessage(errorMessage) {
                                adapter.refresh()
                            }
                        }
                        is LoadState.NotLoading -> placeholderManager.showLoading(false)
                    }
                }
        }
    }

    private fun onClickItemMeli(item: ItemMeli) {
        startActivity(ItemDetailActivity.intentFor(requireContext(), item))
    }

}