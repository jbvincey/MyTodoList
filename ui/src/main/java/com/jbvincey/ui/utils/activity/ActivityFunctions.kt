package com.jbvincey.ui.utils.activity

import android.app.Activity
import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by jbvincey on 02/11/2018.
 */

fun Activity.displaySnack(@StringRes messageRes: Int, vararg formatArgs: Any) {
    hideSoftKeyboard()
    val message = getString(messageRes, *formatArgs)
    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .show()
}

fun Activity.displayActionSnack(@StringRes messageRes: Int,
                               @StringRes actionRes: Int,
                               action: () -> Unit) {
    hideSoftKeyboard()
    Snackbar.make(findViewById(android.R.id.content), messageRes, Snackbar.LENGTH_LONG)
            .setAction(actionRes) { action() }
            .show()
}


fun Activity.hideSoftKeyboard() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}

fun Activity.showSoftKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    view.requestFocus()
    inputMethodManager.showSoftInput(view, 0)
}