package com.luz.melisearch.util

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Luz on 15/8/2022.
 */
class DiffStringCallback(
    private val oldList : List<String>,
    private val newList : List<String>
) : DiffUtil.Callback(){

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    // Check individual items of the list
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    // After areItemsTheSame() is true, check the contents of the list data are the same
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}