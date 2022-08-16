package com.luz.melisearch.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Created by Luz on 16/8/2022.
 */
class PicturesAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: String) {
            Glide.with(itemView)
                .load(item)
                .into(itemView as ImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageHolder(
            ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                adjustViewBounds = true
            }
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ImageHolder).bind(items[position])
    }

}
