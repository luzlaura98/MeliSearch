package com.luz.melisearch.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.luz.melisearch.R
import com.luz.melisearch.utils.parseError
import com.luz.melisearch.databinding.NetworkStateItemBinding

/**
 * Created by Luz on 15/8/2022.
 */
class NetworkStateItemViewHolder(
    parent: ViewGroup,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.network_state_item, parent, false)
) {
    private val binding = NetworkStateItemBinding.bind(itemView)

    init {
        binding.btnRetry.setOnClickListener { retryCallback() }
    }

    fun bindTo(loadState: LoadState) {
        with(binding){
            pbLoading.isVisible = loadState is LoadState.Loading
            btnRetry.isVisible = loadState is LoadState.Error
            tvErrorMessage.text = (loadState as? LoadState.Error)?.error?.parseError(root.context)
            tvErrorMessage.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
        }
    }
}