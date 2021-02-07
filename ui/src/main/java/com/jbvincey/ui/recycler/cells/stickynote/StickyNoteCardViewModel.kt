package com.jbvincey.ui.recycler.cells.stickynote

import androidx.annotation.ColorRes
import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardView.Companion.STICKYNOTE_BACKGROUND_COLOR_1

data class StickyNoteCardViewModel<T>(
    val id: Long,
    val name: CharSequence,
    val notes: List<CharSequence>,
    val callback: StickyNoteCardCallback<T>,
    val data: T,
    @ColorRes val backgroundColorRes: Int = STICKYNOTE_BACKGROUND_COLOR_1
)