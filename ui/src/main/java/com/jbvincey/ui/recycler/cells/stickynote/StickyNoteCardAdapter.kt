package com.jbvincey.ui.recycler.cells.stickynote

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.jbvincey.ui.R
import com.jbvincey.ui.databinding.ViewStickynoteCardBinding
import com.jbvincey.ui.recycler.cells.AbstractCellView
import com.jbvincey.ui.recycler.cells.DataBindingListAdapter

class StickyNoteCardAdapter<T>: DataBindingListAdapter<StickyNoteCardViewModel<T>, ViewStickynoteCardBinding>(DiffCallback()) {

    class DiffCallback<T>: DiffUtil.ItemCallback<StickyNoteCardViewModel<T>>() {

        override fun areItemsTheSame(p0: StickyNoteCardViewModel<T>, p1: StickyNoteCardViewModel<T>) = p0.id == p1.id

        override fun areContentsTheSame(p0: StickyNoteCardViewModel<T>, p1: StickyNoteCardViewModel<T>) = p0 == p1
    }

    override fun getItemViewType(position: Int) = R.layout.view_stickynote_card

    override fun createView(context: Context): AbstractCellView<StickyNoteCardViewModel<T>, ViewStickynoteCardBinding> =
        StickyNoteCardView(context)

    override fun getItem(position: Int): StickyNoteCardViewModel<T> =
        super.getItem(position).copy(
            backgroundColorRes = position.toBackgroundColor()
        )

    private fun Int.toBackgroundColor(): Int = when(this % 3) {
        0 -> StickyNoteCardView.STICKYNOTE_BACKGROUND_COLOR_1
        1 -> StickyNoteCardView.STICKYNOTE_BACKGROUND_COLOR_2
        2 -> StickyNoteCardView.STICKYNOTE_BACKGROUND_COLOR_3
        else -> throw IllegalStateException("Impossible result of $this % 3 (${this % 3})")
    }


}