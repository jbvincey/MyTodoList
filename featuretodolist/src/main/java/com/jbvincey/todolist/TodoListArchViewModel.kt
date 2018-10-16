package com.jbvincey.todolist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.jbvincey.core.models.Todo
import com.jbvincey.core.repositories.TodoRepository
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellCallback
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by jbvincey on 19/09/2018.
 */
class TodoListArchViewModel(private val todoRepository: TodoRepository, todoTransformer: TodoTransformer)
    : ViewModel(), CheckableCellCallback {

    init {
        todoTransformer.checkableCellCallback = this
    }

    private val todoList: LiveData<List<Todo>> = todoRepository.getAllTodos()

    val checkableCellViewModelList: LiveData<List<CheckableCellViewModel>> = Transformations.map(todoList) {
        todoTransformer.transform(it)
    }

    override fun onCheckChanged(id: Long) {
        todoRepository.changeTodoCompleted(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}

