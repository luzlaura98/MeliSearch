package com.luz.melisearch.ui.search

import android.app.Application
import android.app.SearchManager
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.luz.melisearch.application.MyApplication
import com.luz.melisearch.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Luz on 16/8/2022.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val state: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val SAVED_BUNDLE = "savedBundle"
        private const val SAVED_LAST_QUERY = "savedLastQuery"
    }

    /** Save the last query of the user. If app is killed, last query will be available anyway. */
    var lastQuery: String? = null
        set(value) {
            state.setSavedStateProvider(SAVED_BUNDLE) {
                Bundle().apply {
                    putString(SAVED_LAST_QUERY, value)
                }
            }
            field = value
        }

    init {
        val initialState = state.get<Bundle?>(SAVED_BUNDLE)
        lastQuery = initialState?.getString(SAVED_LAST_QUERY)
    }

    private val _suggestionsCursor = MutableLiveData<Resource<Cursor?>>()
    val suggestionsCursor: LiveData<Resource<Cursor?>> get() = _suggestionsCursor

    fun loadSuggestions(query: String) {
        val cursor = getRecentSuggestions(query)
        if (cursor != null)
            _suggestionsCursor.postValue(Resource.Success(cursor))
        else
            _suggestionsCursor.postValue(Resource.Error(null))
    }

    fun clearSuggestions(){
        _suggestionsCursor.postValue(Resource.Success(null))
    }

    private fun getRecentSuggestions(query: String): Cursor? {
        val uriBuilder = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(MySuggestionProvider.AUTHORITY)

        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)

        val selection = " ?"
        val selArgs = arrayOf(query)

        val uri = uriBuilder.build()
        return getApplication<MyApplication>().contentResolver?.query(
            uri,
            null,
            selection,
            selArgs,
            null
        )
    }

    /**
     * Save the query to database [android.database.Cursor]. Then, it will appear as suggestion the next time.
     * */
    fun saveQueryToHistory(query: String) {
        SearchRecentSuggestions(
            getApplication(),
            MySuggestionProvider.AUTHORITY,
            MySuggestionProvider.MODE
        )
            .saveRecentQuery(query, null)
    }

    /**
     * Clear all queries from database. Then, it will not appear suggestions the next time.
     * */
    fun clearHistorySuggestions() {
        SearchRecentSuggestions(
            getApplication(),
            MySuggestionProvider.AUTHORITY,
            MySuggestionProvider.MODE
        )
            .clearHistory()
    }

}