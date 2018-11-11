package com.jbvincey.ui.recycler.cells.helpers

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.View

/**
 * Created by jbvincey on 05/11/2018.
 */
class SwipeController(private val swipeControllerModelStart: SwipeControllerModel? = null,
                      private val swipeControllerModelEnd: SwipeControllerModel? = null,
                      context: Context
) : ItemTouchHelper.SimpleCallback(0,
        (if (swipeControllerModelStart != null) START else 0) or (if (swipeControllerModelEnd != null) END else 0)
) {

    private val isRtL = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

    override fun onMove(recyclerView: RecyclerView,
                        viewHolder1: RecyclerView.ViewHolder,
                        viewHolder2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                          direction: Int) {
        if (direction == START) {
            swipeControllerModelStart?.swipeControllerListener?.onViewSwiped(viewHolder.itemView)
        } else {
            swipeControllerModelEnd?.swipeControllerListener?.onViewSwiped(viewHolder.itemView)
        }
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val itemView = viewHolder.itemView
        return if (itemView is SwipeableView) {
            (if (itemView.isSwipableStart()) START else 0) or (if (itemView.isSwipableEnd()) END else 0)
        } else {
            super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {

        val itemView = viewHolder.itemView

        val dXInt = dX.toInt()
        if (isSwipingStart(dXInt)) {
            if (isRtL) {
                drawBackgroundRight(itemView, c, dXInt, swipeControllerModelStart!!)
                drawActionDrawableRight(itemView, c, swipeControllerModelStart)
            } else {
                drawBackgroundLeft(itemView, c, dXInt, swipeControllerModelStart!!)
                drawActionDrawableLeft(itemView, c, swipeControllerModelStart)
            }
        } else {
            if (isRtL) {
                drawBackgroundLeft(itemView, c, dXInt, swipeControllerModelEnd!!)
                drawActionDrawableLeft(itemView, c, swipeControllerModelEnd)
            } else {
                drawBackgroundRight(itemView, c, dXInt, swipeControllerModelEnd!!)
                drawActionDrawableRight(itemView, c, swipeControllerModelEnd)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun isSwipingStart(dX: Int): Boolean {
        return (dX > 0 && isRtL) || (dX < 0 && !isRtL)
    }

    //region draw background

    private fun drawBackgroundLeft(itemView: View, canvas: Canvas, dX: Int, swipeControllerModel: SwipeControllerModel) {
        drawBackground(itemView.right + dX, itemView.top, itemView.right, itemView.bottom, canvas, swipeControllerModel)
    }

    private fun drawBackgroundRight(itemView: View, canvas: Canvas, dX: Int, swipeControllerModel: SwipeControllerModel) {
        drawBackground(itemView.left, itemView.top, itemView.left + dX, itemView.bottom, canvas, swipeControllerModel)
    }

    private fun drawBackground(left: Int, top: Int, right: Int, bottom: Int, canvas: Canvas, swipeControllerModel: SwipeControllerModel) {
        val backgroundDrawable = swipeControllerModel.backgroundDrawable
        backgroundDrawable.setBounds(left, top, right, bottom)
        backgroundDrawable.draw(canvas)
    }

    //endregion

    //region draw action drawable

    private fun drawActionDrawableLeft(itemView: View, canvas: Canvas, swipeControllerModel: SwipeControllerModel) {
        drawActionDrawable(itemView, canvas, swipeControllerModel, true)
    }

    private fun drawActionDrawableRight(itemView: View, canvas: Canvas, swipeControllerModel: SwipeControllerModel) {
        drawActionDrawable(itemView, canvas, swipeControllerModel, false)
    }

    private fun drawActionDrawable(itemView: View, canvas: Canvas, swipeControllerModel: SwipeControllerModel, drawLeft: Boolean) {
        val actionDrawable = swipeControllerModel.actionDrawable

        if (actionDrawable != null) {
            val itemHeight = itemView.bottom - itemView.top

            val intrinsicWidth = actionDrawable.intrinsicWidth
            val intrinsicHeight = actionDrawable.intrinsicHeight

            val margin = swipeControllerModel.actionMargin

            val actionDrawableLeft = if (drawLeft) itemView.right - (margin + intrinsicWidth) else itemView.left + margin
            val actionDrawableRight = if (drawLeft) itemView.right - margin else  itemView.left + margin + intrinsicWidth
            val actionDrawableTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val actionDrawableBottom = actionDrawableTop + intrinsicHeight
            actionDrawable.setBounds(actionDrawableLeft, actionDrawableTop, actionDrawableRight, actionDrawableBottom)

            actionDrawable.draw(canvas)
        }
    }

    //endregion

}