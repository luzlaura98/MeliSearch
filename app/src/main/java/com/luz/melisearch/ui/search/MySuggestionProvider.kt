package com.luz.melisearch.ui.search

import android.content.SearchRecentSuggestionsProvider

/**
 * Created by Luz on 14/8/2022.
 */
class MySuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.luz.melisearch.ui.search.MySuggestionProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}