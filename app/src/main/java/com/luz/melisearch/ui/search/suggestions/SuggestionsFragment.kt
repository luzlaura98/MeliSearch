package com.luz.melisearch.ui.search.suggestions

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.luz.melisearch.databinding.FragmentSuggestionsBinding
import com.luz.melisearch.ui.adapters.SuggestionAdapter
import com.luz.melisearch.ui.search.OnClickSuggestionListener

/**
 * Created by Luz on 15/8/2022.
 */
class SuggestionsFragment : Fragment() {

    private var _binding : FragmentSuggestionsBinding? = null
    private val binding get() = _binding!!

    private val adapter : SuggestionAdapter by lazy { SuggestionAdapter() }

    fun setOnClickSuggestionListener(onClickSuggestionListener : OnClickSuggestionListener?){
        adapter.onClickSuggestionListener = onClickSuggestionListener
    }

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
    }

    private fun initRecyclerView(){
        binding.recyclerView.adapter = adapter
    }

    fun showSuggestions(cursor : Cursor){
        adapter.load(cursor)
    }

    fun clearSuggestions() {
        adapter.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}