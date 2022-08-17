package com.luz.melisearch.ui.search

import android.app.SearchManager
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.luz.melisearch.R
import com.luz.melisearch.databinding.ActivitySearchBinding
import com.luz.melisearch.ui.base.BaseActivity
import com.luz.melisearch.ui.search.results.ResultsFragment
import com.luz.melisearch.ui.search.suggestions.SuggestionsFragment
import com.luz.melisearch.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Luz on 13/8/2022.
 */
@AndroidEntryPoint
class SearchActivity : BaseActivity(), OnClickSuggestionListener {

    private lateinit var binding: ActivitySearchBinding

    val viewModel by viewModels<SearchViewModel>()

    private var searchView: SearchView? = null

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_search)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        searchView = (menu.findItem(R.id.search).actionView as? SearchView)
        searchView?.init()
        viewModel.lastQuery?.let { searchView?.setQuery(it, false) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.clearSuggestions) {
            viewModel.clearHistorySuggestions()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = ""

        // Listener for onClickSuggestion
        (navHostFragment?.childFragmentManager?.fragments?.get(0) as? SuggestionsFragment)?.apply {
            setOnClickSuggestionListener(this@SearchActivity)
        }

        handleIntent(intent)
    }

    private fun SearchView.init() {
        isIconifiedByDefault = false

        // Full width of the SearchView
        val appBarPadding = resources.getDimensionPixelSize(R.dimen.app_bar_padding)
        val menuItemSize = resources.getDimensionPixelSize(R.dimen.app_bar_size_menu_item)
        maxWidth = binding.toolbar.width - 2 * appBarPadding - menuItemSize

        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.saveQueryToHistory(query)
                    search(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.lastQuery = query
                backToSuggestionsIfNecessary()
                if (!query.isNullOrBlank()) {
                    viewModel.loadSuggestions(query)
                } else {
                    viewModel.clearSuggestions()
                }
                return false
            }
        })

        setOnCloseListener { false }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                viewModel.saveQueryToHistory(query)
                search(query)
            }
        }
    }

    /**
     * If current fragment is [SuggestionsFragment], no need to navigate to that fragment.
     * */
    private fun backToSuggestionsIfNecessary(){
        if (navHostFragment?.childFragmentManager?.fragments?.firstOrNull() !is SuggestionsFragment) {
            findNavController(R.id.nav_host_fragment_content_search)
                .navigate(R.id.action_to_SuggestionsFragment)
        }
    }

    private fun search(query: String) {
        viewModel.lastQuery = query
        hideKeyboard()

        findNavController(R.id.nav_host_fragment_content_search).navigate(
            R.id.resultsFragment,
            bundleOf(ResultsFragment.EXTRA_KEYWORD to query)
        )
    }

    // No need to save query to history
    override fun onClickSuggestion(suggestion: String) {
        searchView?.setQuery(suggestion, false)
        search(suggestion)
    }

}