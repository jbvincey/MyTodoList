package com.jbvincey.ui.recycler.cells.stickynote

interface StickyNoteCardCallback<T> {

    fun onClick(data: StickyNoteCardViewModel<T>)

    fun onLongClick(data: StickyNoteCardViewModel<T>): Boolean

}