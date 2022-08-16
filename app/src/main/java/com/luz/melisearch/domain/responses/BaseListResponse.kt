package com.luz.melisearch.domain.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Luz on 14/8/2022.
 */
data class BaseListResponse<T>(
    @SerializedName("results")
    val results: List<T>,
    @SerializedName("paging")
    val paging: Paging?
)

data class Paging(
    @SerializedName("total")
    val totalItems : Int?
)