package com.luz.melisearch.ui.adapters

import android.app.SearchManager.SUGGEST_COLUMN_TEXT_1
import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.luz.melisearch.databinding.ItemSuggestionBinding
import com.luz.melisearch.ui.search.OnClickSuggestionListener
import com.luz.melisearch.util.DiffStringCallback

/**
 * Created by Luz on 15/8/2022.
 */
class SuggestionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onClickSuggestionListener: OnClickSuggestionListener? = null
    private val items = arrayListOf<String>()

    fun clear() {
        val oldSize = items.size
        if (oldSize > 0) {
            items.clear()
            notifyItemRangeRemoved(0, oldSize)
        }
    }

    fun load(cursor: Cursor) {
        cursor.moveToFirst()
        val newItems = arrayListOf<String>()
        while (!cursor.isAfterLast) {
            val value = cursor.getString(cursor.getColumnIndexOrThrow(SUGGEST_COLUMN_TEXT_1))
            newItems.add(value)
            cursor.moveToNext()
        }

        val diffCallback = DiffStringCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    private class SuggestionViewHolder(
        private val binding: ItemSuggestionBinding,
        var onClickSuggestionListener: OnClickSuggestionListener?
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvTitle.text = item
            binding.root.setOnClickListener {
                onClickSuggestionListener?.onClickSuggestion(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SuggestionViewHolder(
            ItemSuggestionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), onClickSuggestionListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SuggestionViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}