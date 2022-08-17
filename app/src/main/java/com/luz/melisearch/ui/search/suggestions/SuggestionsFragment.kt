package com.luz.melisearch.ui.search.suggestions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luz.melisearch.databinding.FragmentSuggestionsBinding
import com.luz.melisearch.ui.adapters.SuggestionAdapter
import com.luz.melisearch.ui.base.BaseFragment
import com.luz.melisearch.ui.search.OnClickSuggestionListener
import com.luz.melisearch.ui.search.SearchActivity
import com.luz.melisearch.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Luz on 15/8/2022.
 */
@AndroidEntryPoint
class SuggestionsFragment : BaseFragment() {

    private var _binding : FragmentSuggestionsBinding? = null
    private val binding get() = _binding!!

    private val adapter : SuggestionAdapter by lazy { SuggestionAdapter() }

    private val sharedViewModel by lazy { (activity as SearchActivity).viewModel }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuggestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        sharedViewModel.suggestionsCursor.observe(viewLifecycleOwner){ resource ->
            if (resource is Resource.Success){
                if (resource.data != null)
                    adapter.load(resource.data)
                else
                    adapter.clear()
            }  else if( resource is Resource.Error){
                showSnackErrorMessage(resource.message)
            }
        }
    }

    private fun initRecyclerView(){
        binding.recyclerView.adapter = adapter
    }

    fun setOnClickSuggestionListener(onClickSuggestionListener : OnClickSuggestionListener?){
        adapter.onClickSuggestionListener = onClickSuggestionListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}