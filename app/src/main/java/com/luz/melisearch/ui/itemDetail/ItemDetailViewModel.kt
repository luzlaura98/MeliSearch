package com.luz.melisearch.ui.itemDetail

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import com.luz.melisearch.domain.entities.ItemMeliDetail
import com.luz.melisearch.domain.repo.ItemsRepository
import com.luz.melisearch.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Luz on 15/8/2022.
 */
@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val state: SavedStateHandle,
    application: Application,
    private val repository: ItemsRepository
) : AndroidViewModel(application) {

    companion object {
        private const val SAVED_BUNDLE = "savedBundle"
        private const val SAVED_ITEM = "savedItem"
    }

    /** Saved item detail. It prevents call API to get the data again if there was
     *  called before and app process was killed. */
    private var itemDetail: ItemMeliDetail? = null

    init {
        val initialState = state.get<Bundle?>(SAVED_BUNDLE)
        itemDetail = initialState?.getParcelable(SAVED_ITEM)
    }

    private val _itemLiveData = MutableLiveData<Resource<ItemMeliDetail>>()
    val itemLiveData: LiveData<Resource<ItemMeliDetail>> get() = _itemLiveData

    fun getItemDetail(itemId: String) {
        if (itemDetail != null){
            _itemLiveData.postValue(Resource.Success(itemDetail!!))
            return
        }

        _itemLiveData.postValue(Resource.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            repository.getItemDetail(itemId).collect { resource ->
                _itemLiveData.postValue(resource)
                if (resource is Resource.Success && resource.data != null) {
                    state.setSavedStateProvider(SAVED_BUNDLE){
                        Bundle().apply {
                            putParcelable(SAVED_ITEM, resource.data)
                        }
                    }
                }
            }
        }
    }
}