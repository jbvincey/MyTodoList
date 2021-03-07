package com.jbvincey.ui.recycler.cells.stickynote

import android.view.View

interface StickyNoteCardCallback<T> {

    fun onClick(data: StickyNoteCardViewModel<T>, view: View)

    fun onLongClick(data: StickyNoteCardViewModel<T>, view: View): Boolean

}