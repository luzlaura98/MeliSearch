package com.luz.melisearch.ui.search.results

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.luz.melisearch.data.constants.AppConstants.ITEMS_PER_PAGE
import com.luz.melisearch.domain.repo.ItemsPagingSource
import com.luz.melisearch.domain.repo.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Luz on 15/8/2022.
 */
@HiltViewModel
class ResultsViewModel @Inject constructor(
    //application: Application,
    private val repository : ItemsRepository
): ViewModel() {
    lateinit var keyword : String

    val myFlow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = ITEMS_PER_PAGE)
    ){
        ItemsPagingSource(keyword, repository.appService)
    }.flow
        .cachedIn(viewModelScope)
}