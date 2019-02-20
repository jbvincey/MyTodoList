package com.jbvincey.ui.appbar

import android.animation.ValueAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.jbvincey.ui.R

/**
 * Created by jbvincey on 22/10/2018.
 */
class AppbarElevationRecyclerScrollListener(
        private val appBarLayout: AppBarLayout,
        recyclerView: RecyclerView
) : RecyclerView.OnScrollListener() {

    companion object {
        const val SCROLL_DIRECTION_UP = -1
        const val ELEVATION_ANIMATION_DURATION = 300L
    }

    private val displayElevationValueAnimator: ValueAnimator
    private val hideElevationValueAnimator: ValueAnimator

    private var isDisplayingAppBarElevation = false

    init {
        val recyclerViewDefaultElevation = recyclerView.elevation
        appBarLayout.elevation = recyclerViewDefaultElevation
        val appbarElevation = recyclerViewDefaultElevation + appBarLayout.resources.getDimension(R.dimen.appbar_elevation)

        displayElevationValueAnimator = ValueAnimator.ofFloat(recyclerViewDefaultElevation, appbarElevation)
        displayElevationValueAnimator.duration = ELEVATION_ANIMATION_DURATION
        displayElevationValueAnimator.addUpdateListener { valueAnimator ->
            appBarLayout.elevation = valueAnimator.animatedValue as Float
        }

        hideElevationValueAnimator = ValueAnimator.ofFloat(appbarElevation, recyclerViewDefaultElevation)
        hideElevationValueAnimator.duration = ELEVATION_ANIMATION_DURATION
        hideElevationValueAnimator.addUpdateListener { valueAnimator ->
            appBarLayout.elevation = valueAnimator.animatedValue as Float
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val canScrollVertically = recyclerView.canScrollVertically(SCROLL_DIRECTION_UP)

        if (canScrollVertically && !isDisplayingAppBarElevation) {
            isDisplayingAppBarElevation = true
            hideElevationValueAnimator.cancel()
            displayElevationValueAnimator.start()

        } else if (!canScrollVertically && isDisplayingAppBarElevation) {
            isDisplayingAppBarElevation = false
            displayElevationValueAnimator.cancel()
            hideElevationValueAnimator.start()
        }
    }


}