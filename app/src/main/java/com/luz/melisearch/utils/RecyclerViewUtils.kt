package com.luz.melisearch.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Luz on 16/8/2022.
 */
fun RecyclerView.isFirstItem(view: View): Boolean {
    return getChildAdapterPosition(view) == 0
}