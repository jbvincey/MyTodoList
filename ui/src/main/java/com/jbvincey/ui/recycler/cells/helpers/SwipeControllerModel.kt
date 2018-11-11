package com.jbvincey.ui.recycler.cells.helpers

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt

/**
 * Created by jbvincey on 08/11/2018.
 */
class SwipeControllerModel(
        @ColorInt private val backgroundColor: Int,
        val actionDrawable: Drawable?,
        val actionMargin: Int,
        val swipeControllerListener: SwipeControllerListener
) {

    val backgroundDrawable = ColorDrawable(backgroundColor)

}