package com.jbvincey.featureaddtodo.edittodo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.jbvincey.core.models.Todo
import com.jbvincey.core.repositories.TodoRepository
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by jbvincey on 27/10/2018.
 */
class EditTodoArchViewModel(private val todoRepository: TodoRepository): ViewModel() {

    val todoId: MutableLiveData<Long> = MutableLiveData()
    val todo: LiveData<Todo> = Transformations.switchMap(todoId) {todoId ->
        todoRepository.getTodoById(todoId)
    }
    val editTodoState = MutableLiveData<EditTodoState>()
    val deleteTodoState = MutableLiveData<DeleteTodoState>()
    val archiveTodoState = MutableLiveData<ArchiveTodoState>()
    val unarchiveTodoState = MutableLiveData<UnarchiveTodoState>()

    fun editTodo(todoName: String) {
        todoRepository.editTodo(todoName, todoId.value!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { editTodoState.value = EditTodoState.Success },
                        { editTodoState.value = EditTodoState.UnknownError }
                )
    }

    fun deleteTodo() {
        todoRepository.deleteTodo(todoId.value!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { deleteTodoState.value = DeleteTodoState.Success },
                        { deleteTodoState.value = DeleteTodoState.UnknownError }
                )
    }

    fun archiveTodo() {
        todoRepository.archiveTodo(todoId.value!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { archiveTodoState.value = ArchiveTodoState.Success },
                        { archiveTodoState.value = ArchiveTodoState.UnknownError }
                )
    }

    fun unarchiveTodo() {
        todoRepository.unarchiveTodo(todoId.value!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { unarchiveTodoState.value = UnarchiveTodoState.Success },
                        { unarchiveTodoState.value = UnarchiveTodoState.UnknownError }
                )
    }
}

sealed class EditTodoState {
    object Success : EditTodoState()
    object UnknownError : EditTodoState()
}

sealed class DeleteTodoState {
    object Success : DeleteTodoState()
    object UnknownError : DeleteTodoState()
}

sealed class ArchiveTodoState {
    object Success : ArchiveTodoState()
    object UnknownError : ArchiveTodoState()
}

sealed class UnarchiveTodoState {
    object Success : UnarchiveTodoState()
    object UnknownError : UnarchiveTodoState()
}