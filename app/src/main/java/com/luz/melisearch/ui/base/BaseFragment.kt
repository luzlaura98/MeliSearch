package com.luz.melisearch.ui.base

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.luz.melisearch.R

/**
 * Created by Luz on 16/8/2022.
 */
open class BaseFragment : Fragment(){

    private var snackErrorMessage: Snackbar? = null

    fun showSnackErrorMessage(message: String? = null) {
        view?:return
        if (snackErrorMessage == null) {
            snackErrorMessage = Snackbar.make(requireView(),
                message ?: getString(R.string.error_default_message),
                Snackbar.LENGTH_INDEFINITE
            )
            snackErrorMessage?.apply {
                setAction(getString(R.string.ok_)) { snackErrorMessage?.dismiss() }
                show()
            }
        }
        snackErrorMessage?.apply {
            setText(message ?: getString(R.string.error_default_message))
            show()
        }
    }
}