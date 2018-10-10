package com.jbvincey.todolist

import com.jbvincey.core.models.Todo
import com.jbvincey.core.transformers.Transformer
import com.jbvincey.ui.recycler.cells.todo.TodoViewModel

/**
 * Created by jbvincey on 28/09/2018.
 */
class TodoViewModelTransformer: Transformer<Todo, TodoViewModel> {

    override fun transform(source: Todo): TodoViewModel {
        return TodoViewModel(
                source.id,
                source.name,
                source.completed,
                source.creationDate,
                source.completionDate,
                source.archived
        )
    }


}