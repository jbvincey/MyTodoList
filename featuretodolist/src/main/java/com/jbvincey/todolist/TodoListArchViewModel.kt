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
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * Created by jbvincey on 19/09/2018.
 */
class TodoListArchViewModel(private val todoRepository: TodoRepository,
                            private val todoTransformer: TodoTransformer)
    : ViewModel(), CheckableCellCallback {

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

    private val disposables = CompositeDisposable()
    private val todoListType = MutableLiveData<TodoListType>()
    val todoClicked: MutableLiveData<Long> = MutableLiveData()

    val deleteTodoState = MutableLiveData<DeleteTodoState>()
    val archiveTodoState = MutableLiveData<ArchiveTodoState>()

    init {
        todoTransformer.checkableCellCallback = this
    }

    private val todoList: LiveData<List<Todo>> = Transformations.switchMap(todoListType) { type ->
        when(type) {
            TodoListType.UNARCHIVED ->todoRepository.getAllUnarchivedTodos()
            TodoListType.ARCHIVED -> todoRepository.getAllArchivedTodos()
        }
    }
    val checkableCellViewModelList: LiveData<List<CheckableCellViewModel>> = Transformations.map(todoList) {
        sortAndTransformTodoList(it)
    }

    private fun sortAndTransformTodoList(todoList: List<Todo>): List<CheckableCellViewModel> {
        val sortedTodoList = todoList.toMutableList()
        sortedTodoList.sortWith(TODO_COMPARATOR)
        return todoTransformer.transform(sortedTodoList)
    }

    override fun onCheckChanged(id: Long) {
        disposables.add(todoRepository.updateTodoCompleted(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    override fun onClick(id: Long) {
        todoClicked.value = id
    }

    fun showUnarchivedTodos() {
        todoListType.value = TodoListType.UNARCHIVED
    }

    fun showArchivedTodos() {
        todoListType.value = TodoListType.ARCHIVED
    }

    fun isShowingAchivedTodos(): Boolean {
        return todoListType.value == TodoListType.ARCHIVED
    }

    fun deleteTodo(todoId: Long) {
        disposables.add(todoRepository.deleteTodo(todoId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { deleteTodoState.value = DeleteTodoState.Success },
                        { deleteTodoState.value = DeleteTodoState.UnknownError }
                ))
    }

    fun archiveTodo(todoId: Long) {
        disposables.add(todoRepository.archiveTodo(todoId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { archiveTodoState.value = ArchiveTodoState.Success },
                        { archiveTodoState.value = ArchiveTodoState.UnknownError }
                ))
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}

enum class TodoListType {
    UNARCHIVED,
    ARCHIVED
}

sealed class DeleteTodoState {
    object Success : DeleteTodoState()
    object UnknownError : DeleteTodoState()
}

sealed class ArchiveTodoState {
    object Success : ArchiveTodoState()
    object UnknownError : ArchiveTodoState()
}

