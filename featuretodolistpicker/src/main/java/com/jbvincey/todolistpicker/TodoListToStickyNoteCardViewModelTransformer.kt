package com.jbvincey.todolistpicker

import com.jbvincey.core.models.TodoListWithTodos
import com.jbvincey.core.transformers.Transformer
import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardCallback
import com.jbvincey.ui.recycler.cells.stickynote.StickyNoteCardViewModel

class TodoListToStickyNoteCardViewModelTransformer: Transformer<TodoListWithTodos, StickyNoteCardViewModel<TodoListWithTodos>> {

    lateinit var stickyNoteCallback: StickyNoteCardCallback<TodoListWithTodos>

    override fun transform(source: TodoListWithTodos) = StickyNoteCardViewModel(
        id = source.todoList.id,
        name = source.todoList.name,
        notes = source.todos
            .filter { todo -> !todo.completed && !todo.deleted }
            .map { it.name },
        callback = stickyNoteCallback,
        data = source
    )

}