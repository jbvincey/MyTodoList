package com.jbvincey.core.utils

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

fun Menu.add(menuId: Int, @StringRes menuTextRest: Int, @DrawableRes menuIconRes: Int) {
    this.add(0, menuId, Menu.NONE, menuTextRest).setIcon(menuIconRes).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
}