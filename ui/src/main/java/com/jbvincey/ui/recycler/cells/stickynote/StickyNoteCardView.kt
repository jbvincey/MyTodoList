package com.jbvincey.ui.recycler.cells.stickynote

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import com.jbvincey.ui.R
import com.jbvincey.ui.databinding.ViewStickynoteCardBinding
import com.jbvincey.ui.recycler.cells.AbstractCellView

class StickyNoteCardView<T> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractCellView<StickyNoteCardViewModel<T>, ViewStickynoteCardBinding>(context, attrs, defStyleAttr) {

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    override fun getLayout() = R.layout.view_stickynote_card

    //force view to be square, setting height equals to width
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    companion object {
        @ColorRes
        val STICKYNOTE_BACKGROUND_COLOR_1: Int = R.color.pink_900

        @ColorRes
        val STICKYNOTE_BACKGROUND_COLOR_2: Int = R.color.blue_900

        @ColorRes
        val STICKYNOTE_BACKGROUND_COLOR_3: Int = R.color.green_900
    }

}

@BindingAdapter("notes")
fun TextView.bindNotes(notes: List<CharSequence>) {
    var description = ""
    notes.forEach { note -> description = "$description -$note\n" }
    text = description
}

