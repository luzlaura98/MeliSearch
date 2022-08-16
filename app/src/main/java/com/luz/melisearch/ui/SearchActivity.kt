package com.luz.melisearch.ui

import android.app.SearchManager
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.luz.melisearch.R
import com.luz.melisearch.databinding.ActivitySearchBinding
import com.luz.melisearch.ui.base.BaseActivity
import com.luz.melisearch.ui.search.OnClickSuggestionListener
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

    private var suggestionsFragment: SuggestionsFragment? = null
    private var searchView: SearchView? = null

    private var lastQuery : String? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        searchView = (menu.findItem(R.id.search).actionView as? SearchView)
        searchView?.init()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.clearSuggestions) {
            clearHistorySuggestions()
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

        handleIntent(intent)

        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_search)
        suggestionsFragment =
            navHostFragment?.childFragmentManager?.fragments?.get(0) as? SuggestionsFragment
        suggestionsFragment?.setOnClickSuggestionListener(this)
    }

    // Function To Retrieve Suggestion From Content Resolver
    fun getRecentSuggestions(query: String): Cursor? {
        val uriBuilder = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(MySuggestionProvider.AUTHORITY)

        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)

        val selection = " ?"
        val selArgs = arrayOf(query)

        val uri = uriBuilder.build()
        return contentResolver?.query(uri, null, selection, selArgs, null)
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
                    saveQueryToHistory(query)
                    search(query)
                    hideKeyboard()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                lastQuery = query
                val navHost =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_search)
                if (navHost?.childFragmentManager?.fragments?.firstOrNull() != suggestionsFragment) {
                    findNavController(R.id.nav_host_fragment_content_search)
                        .navigate(R.id.action_to_SuggestionsFragment)
                }
                if (!query.isNullOrBlank()) {
                    getRecentSuggestions(query)?.let { showSuggestions(it) }
                } else {
                    clearSuggestions()
                }
                return false
            }
        })

        setOnCloseListener { false }

        lastQuery?.let {
            setQuery(it, false)
        }
    }

    private fun showSuggestions(cursor: Cursor) {
        suggestionsFragment?.showSuggestions(cursor)
    }

    private fun clearSuggestions() {
        suggestionsFragment?.clearSuggestions()
    }

    private fun saveQueryToHistory(query: String) {
        SearchRecentSuggestions(
            this,
            MySuggestionProvider.AUTHORITY,
            MySuggestionProvider.MODE
        )
            .saveRecentQuery(query, null)
    }

    private fun clearHistorySuggestions() {
        SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE)
            .clearHistory()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                saveQueryToHistory(query)
                search(query)
            }
        }
    }

    private fun search(query: String) {
        lastQuery = query
        findNavController(R.id.nav_host_fragment_content_search).navigate(
            R.id.resultsFragment, //action //resultsFragment
            bundleOf(ResultsFragment.EXTRA_KEYWORD to query)
        )
    }

    // No need to save query to history
    override fun onClickSuggestion(suggestion: String) {
        searchView?.setQuery(suggestion, false)
        search(suggestion)
    }

}