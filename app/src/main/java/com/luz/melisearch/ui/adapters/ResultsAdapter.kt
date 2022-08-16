package com.luz.melisearch.ui.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.color.MaterialColors
import com.luz.melisearch.domain.entities.ItemMeli
import com.luz.melisearch.databinding.ItemMeliBinding
import com.luz.melisearch.utils.toPriceFormat

/**
 * Created by Luz on 15/8/2022.
 */
class ResultsAdapter(
    private val onClick : (item: ItemMeli) -> Unit
) : PagingDataAdapter<ItemMeli, ItemMeliViewHolder>(POST_COMPARATOR) {

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<ItemMeli>() {
            override fun areItemsTheSame(oldItem: ItemMeli, newItem: ItemMeli) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: ItemMeli, newItem: ItemMeli) =
                oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMeliViewHolder {
        return ItemMeliViewHolder.create(parent, onClick)
    }

    override fun onBindViewHolder(holder: ItemMeliViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ItemMeliViewHolder(
    private val binding: ItemMeliBinding,
    private val onClick: (item: ItemMeli) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        /**
         * Create the view holder inflating the appropriate layout.
         * */
        fun create(
            parent: ViewGroup,
            onClick: (item: ItemMeli) -> Unit
        ): ItemMeliViewHolder {
            val binding = ItemMeliBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ItemMeliViewHolder(binding, onClick)
        }
    }

    fun bind(item: ItemMeli?) {
        with(binding) {
            root.setOnClickListener { item?.let { onClick(item) } }
            tvTitle.text = item?.title
            tvPrice.text = item?.price?.toPriceFormat()
            Glide.with(root)
                .load(item?.thumbnail)
                .placeholder(ColorDrawable(MaterialColors.getColor(itemView.context, com.google.android.material.R.attr.colorPrimaryInverse, Color.GREEN)))
                .into(ivThumbnail)
        }
    }
}