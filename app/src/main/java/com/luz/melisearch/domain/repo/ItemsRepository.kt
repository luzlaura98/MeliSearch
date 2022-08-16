package com.luz.melisearch.domain.repo

import android.app.Application
import com.luz.melisearch.data.services.MeliAPI
import com.luz.melisearch.domain.entities.ItemMeliDetail
import com.luz.melisearch.utils.Resource
import com.luz.melisearch.utils.parseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Luz on 15/8/2022.
 */
class ItemsRepository @Inject constructor(
    private val application: Application,
    val appService : MeliAPI
){
    fun getItemDetail(itemId : String) : Flow<Resource<ItemMeliDetail>>{
        return flow {
            try {
                val response = appService.getDetailItem(listOf(itemId))
                if (response.isEmpty() || response.first().code != 200)
                    throw IllegalStateException()
                else emit(Resource.Success(response.first().body))
            } catch (e : Exception){
                e.printStackTrace()
                emit(Resource.Error<ItemMeliDetail>(e.parseError(application)))
            }
        }
    }
}