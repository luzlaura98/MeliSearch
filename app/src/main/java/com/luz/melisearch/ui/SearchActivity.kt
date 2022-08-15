package com.luz.melisearch.ui

import android.app.SearchManager
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.luz.melisearch.R
import com.luz.melisearch.databinding.ActivitySearchBinding
import com.luz.melisearch.ui.adapters.SuggestionAdapter
import com.luz.melisearch.ui.base.BaseActivity
import com.luz.melisearch.ui.search.OnClickSuggestionListener
import com.luz.melisearch.ui.search.SuggestionsFragment


/**
 * Created by Luz on 13/8/2022.
 */
class SearchActivity : BaseActivity(), OnClickSuggestionListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding : ActivitySearchBinding

    private var suggestionsFragment : SuggestionsFragment? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        (menu.findItem(R.id.search).actionView as? SearchView)?.init()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.clearSuggestions){
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

        /*val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        handleIntent(intent)

        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_search)
        suggestionsFragment = navHostFragment?.childFragmentManager?.fragments?.get(0) as? SuggestionsFragment
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

    private fun SearchView.init(){
        isIconifiedByDefault = false

        // Full width of the SearchView
        val appBarPadding = resources.getDimensionPixelSize(R.dimen.app_bar_padding)
        val menuItemSize = resources.getDimensionPixelSize(R.dimen.app_bar_size_menu_item)
        maxWidth = binding.toolbar.width - 2 * appBarPadding - menuItemSize

        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()){
                    saveQueryToHistory(query)
                    search(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrBlank())
                    getRecentSuggestions(query)?.let { showSuggestions(it) }
                else
                    clearSuggestions()
                return false
            }
        })

        setOnCloseListener { false }
    }

    private fun showSuggestions(cursor : Cursor){
        suggestionsFragment?.showSuggestions(cursor)
    }

    private fun clearSuggestions(){
        suggestionsFragment?.clearSuggestions()
    }

    private fun saveQueryToHistory(query : String){
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
        findNavController(R.id.nav_host_fragment_content_search).navigate(
            R.id.action_SuggestionsFragment_to_ResultsFragment,
            bundleOf("keyword" to query)
        )
    }

    // No need to save query to history
    override fun onClickSuggestion(suggestion: String) {
        search(suggestion)
    }

}