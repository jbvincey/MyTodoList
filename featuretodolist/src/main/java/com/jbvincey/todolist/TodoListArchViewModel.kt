package com.jbvincey.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
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
                            private val todoToCheckableCellViewModelTransformer: TodoToCheckableCellViewModelTransformer)
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
    val undeleteTodoState = MutableLiveData<UndeleteTodoState>()
    val archiveTodoState = MutableLiveData<ArchiveTodoState>()
    val unarchiveTodoState = MutableLiveData<UnarchiveTodoState>()

    init {
        todoToCheckableCellViewModelTransformer.checkableCellCallback = this
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
        return todoToCheckableCellViewModelTransformer.transform(sortedTodoList)
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
                        { deleteTodoState.value = DeleteTodoState.Success(todoId, getTodoName(todoId)) },
                        { deleteTodoState.value = DeleteTodoState.UnknownError(todoId) }
                ))
    }

    fun undeleteTodo(todoId: Long) {
        disposables.add(todoRepository.undeleteTodo(todoId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { undeleteTodoState.value = UndeleteTodoState.Success },
                        { undeleteTodoState.value = UndeleteTodoState.UnknownError(todoId) }
                ))
    }

    fun archiveTodo(todoId: Long) {
        disposables.add(todoRepository.archiveTodo(todoId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { archiveTodoState.value = ArchiveTodoState.Success(todoId, getTodoName(todoId)) },
                        { archiveTodoState.value = ArchiveTodoState.UnknownError(todoId) }
                ))
    }

    fun unarchiveTodo(todoId: Long) {
        disposables.add(todoRepository.unarchiveTodo(todoId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { unarchiveTodoState.value = UnarchiveTodoState.Success },
                        { unarchiveTodoState.value = UnarchiveTodoState.UnknownError(todoId) }
                ))
    }

    private fun getTodoName(todoId: Long): String {
        return todoList.value!!.find { todo -> todo.id == todoId }!!.name
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
    class Success(val todoId: Long, val todoName: String) : DeleteTodoState()
    class UnknownError(val todoId: Long) : DeleteTodoState()
}

sealed class UndeleteTodoState {
    object Success : UndeleteTodoState()
    class UnknownError(val todoId: Long) : UndeleteTodoState()
}

sealed class ArchiveTodoState {
    class Success(val todoId: Long, val todoName: String) : ArchiveTodoState()
    class UnknownError(val todoId: Long) : ArchiveTodoState()
}

sealed class UnarchiveTodoState {
    object Success : UnarchiveTodoState()
    class UnknownError(val todoId: Long) : UnarchiveTodoState()
}
