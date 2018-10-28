package com.jbvincey.todolist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.jbvincey.core.models.Todo
import com.jbvincey.core.repositories.TodoRepository
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellCallback
import com.jbvincey.ui.recycler.cells.checkablecell.CheckableCellViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

/**
 * Created by jbvincey on 19/09/2018.
 */
class TodoListArchViewModel(private val todoRepository: TodoRepository,
                            private val todoTransformer: TodoTransformer)
    : ViewModel(), CheckableCellCallback {

    init {
        todoTransformer.checkableCellCallback = this
    }

    companion object {

        // comparator sorting Todos: uncompleted todos fist and completed last, then sort by creation date
        val TODO_COMPARATOR = Comparator<Todo> { t1, t2 ->
            if (t1.completed != t2.completed) {
                if (t1.completed) 1 else -1
            } else {
                (t1.creationDate.time - t2.creationDate.time).toInt()
            }
        }
    }

    private val todoList: LiveData<List<Todo>> = todoRepository.getAllTodos()
    val checkableCellViewModelList: LiveData<List<CheckableCellViewModel>> = Transformations.map(todoList) {
        sortAndTransformTodoList(it)
    }

    val todoClicked: MutableLiveData<Long> = MutableLiveData()

    private fun sortAndTransformTodoList(todoList: List<Todo>): List<CheckableCellViewModel> {
        val sortedTodoList = todoList.toMutableList()
        sortedTodoList.sortWith(TODO_COMPARATOR)
        return todoTransformer.transform(sortedTodoList)
    }

    override fun onCheckChanged(id: Long) {
        todoRepository.changeTodoCompleted(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun onClick(id: Long) {
        todoClicked.value = id
    }
}

