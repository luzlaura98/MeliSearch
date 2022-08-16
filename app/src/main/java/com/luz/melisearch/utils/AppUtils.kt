package com.luz.melisearch.utils

/**
 * Created by Luz on 16/8/2022.
 */
fun Float.toPriceFormat() : String{
    return String.format("$ %.2f", this)
}