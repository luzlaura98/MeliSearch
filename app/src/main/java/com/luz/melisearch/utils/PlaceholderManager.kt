package com.luz.melisearch.utils

import android.view.View
import androidx.core.view.isVisible
import com.luz.melisearch.R
import com.luz.melisearch.databinding.PartialPlaceholderBinding

/**
 * Created by Luz on 15/8/2022.
 * @param binding a PartialPlaceholderBinding included inside your layout.
 * @param viewsToHide views that you need to hide when placeholder is showing.
 */
class PlaceholderManager(
    private val binding: PartialPlaceholderBinding,
    vararg viewsToHide : View
) {

    private val views : List<View>
    init {
        val list = arrayListOf<View>()
        for (v in viewsToHide) list.add(v)
        views = list
    }

    /**
     * [PartialPlaceholderBinding] visible or not.
     * @param isLoading if it is true, shows loading and hides the others views. If it is false,
     * only shows the [views] declared to hide when placeholder is showed. For example, a recyclerView.
     * */
    fun showLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                root.isVisible = true
                pbLoading.isVisible = true
                ivPlaceholder.isVisible = false
                tvPlaceholderTitle.isVisible = false
                tvPlaceholderMessage.isVisible = false
                btnRetry.isVisible = false
                for (v in views) v.isVisible = false
            } else{
                root.isVisible = false
                for (v in views) v.isVisible = true
            }
        }
    }

    /**
     * [PartialPlaceholderBinding] visible. It shows title, message and retry button (if it is
     * needed).
     * @param message message to show. Default message is [R.string.error_default_message].
     * @param onRetry retry function. If it is null, retry button is not showed.
     * */
    fun showPlaceholderMessage(message: String?, onRetry: (() -> Unit)?) {
        with(binding) {
            root.isVisible = true
            pbLoading.isVisible = false
            ivPlaceholder.isVisible = true
            tvPlaceholderTitle.isVisible = true
            tvPlaceholderMessage.text =
                message ?: root.context.getString(R.string.error_default_message)
            tvPlaceholderMessage.isVisible = true
            btnRetry.isVisible =
                if (onRetry != null) {
                    btnRetry.setOnClickListener { onRetry.invoke() }
                    true
                } else
                    false
            for (v in views) v.isVisible = false
        }
    }
}