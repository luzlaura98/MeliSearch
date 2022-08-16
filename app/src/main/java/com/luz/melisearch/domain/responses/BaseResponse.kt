package com.luz.melisearch.domain.responses

/**
 * Created by Luz on 14/8/2022.
 */
data class BaseResponse<T>(
    val code : Int,
    val body : T
)
