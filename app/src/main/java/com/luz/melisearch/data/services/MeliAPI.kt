package com.luz.melisearch.data.services

import com.luz.melisearch.data.constants.AppConstants.ITEMS_PER_PAGE
import com.luz.melisearch.data.constants.AppConstants.SITE_ID
import com.luz.melisearch.domain.responses.BaseListResponse
import com.luz.melisearch.domain.responses.BaseResponse
import com.luz.melisearch.domain.entities.ItemMeli
import com.luz.melisearch.domain.entities.ItemMeliDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Luz on 13/8/2022.
 */
interface MeliAPI {

    @GET("sites/{siteId}/search")
    suspend fun search(
        @Path("siteId") siteId: String = SITE_ID,
        @Query("q") keyword: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = ITEMS_PER_PAGE
    ): BaseListResponse<ItemMeli>

    @GET("items")
    suspend fun getDetailItem(
        @Query("ids") ids: List<String>
    ): List<BaseResponse<ItemMeliDetail>>

}