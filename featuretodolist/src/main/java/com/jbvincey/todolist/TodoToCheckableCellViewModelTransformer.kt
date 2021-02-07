package com.jbvincey.todolist

import com.jbvincey.core.models.Todo
import com.jbvincey.core.transformers.Transformer
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellCallback
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellViewModel

/**
 * Created by jbvincey on 28/09/2018.
 */
class TodoToCheckableCellViewModelTransformer : Transformer<Todo, CheckableCellViewModel<Todo>> {

    lateinit var checkableCellCallback: CheckableCellCallback<Todo>

    var backgroundColorRes: Int = R.color.theme_background_1

    override fun transform(source: Todo): CheckableCellViewModel<Todo>
        = CheckableCellViewModel(
            id = source.id,
            name = source.name,
            completed = source.completed,
            creationDate = source.creationDate,
            completionDate = source.completionDate,
            archived = source.archived,
            backgroundColorRes = backgroundColorRes,
            callback = checkableCellCallback,
            data = source
        )

}