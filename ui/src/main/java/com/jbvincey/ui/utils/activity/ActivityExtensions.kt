package com.jbvincey.ui.utils.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jbvincey.ui.R

/**
 * Created by jbvincey on 02/11/2018.
 */

fun Activity.displaySnack(@StringRes messageRes: Int, vararg formatArgs: Any) {
    hideSoftKeyboard()
    val message = getString(messageRes, *formatArgs)
    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .show()
}

fun Activity.displayActionSnack(
    @StringRes messageRes: Int,
    @StringRes actionRes: Int,
    vararg formatArgs: Any,
    action: () -> Unit
) {
    hideSoftKeyboard()
    val message = getString(messageRes, *formatArgs)
    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setAction(actionRes) { action() }
            .show()
}

fun Activity.displayAlertDialog(
    @StringRes messageRes: Int,
    @StringRes actionRes: Int,
    vararg formatArgs: Any,
    action: () -> Unit
) {
    hideSoftKeyboard()
    val message = getString(messageRes, *formatArgs)

    MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton(actionRes) { dialog: DialogInterface, _: Int ->
                run {
                    action()
                    dialog.cancel()
                }
            }
            .setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
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

fun AppCompatActivity.showSoftKeyboardWithDelay(
    view: View,
    delayMillis: Long
) {
    val handler = Handler()
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun removeHandlerCallbacks() {
            handler.removeCallbacksAndMessages(null)
        }
    })
    handler.postDelayed(
        { showSoftKeyboard(view) },
        delayMillis
    )
}