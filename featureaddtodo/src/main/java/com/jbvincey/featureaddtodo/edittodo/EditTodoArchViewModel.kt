package com.jbvincey.featureaddtodo.edittodo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.models.Todo
import com.jbvincey.core.repositories.TodoRepository
import kotlinx.coroutines.launch

/**
 * Created by jbvincey on 27/10/2018.
 */
class EditTodoArchViewModel(private val todoRepository: TodoRepository): ViewModel() {

    val todoId: MutableLiveData<Long> = MutableLiveData()
    val todo: LiveData<Todo> = Transformations.switchMap(todoId) { todoId ->
        todoRepository.getTodoById(todoId)
    }
    val editTodoState = MutableLiveData<EditTodoState>()
    val deleteTodoState = MutableLiveData<DeleteTodoState>()
    val archiveTodoState = MutableLiveData<ArchiveTodoState>()
    val unarchiveTodoState = MutableLiveData<UnarchiveTodoState>()

    fun editTodo(todoName: String) {
        viewModelScope.launch {
            try {
                todoRepository.editTodo(todoName, todoId.value!!)
                editTodoState.value = EditTodoState.Success
            } catch (e: Exception) {
                editTodoState.value = EditTodoState.UnknownError
            }
        }
    }

    fun deleteTodo() {
        viewModelScope.launch {
            try {
                todoRepository.deleteTodo(todoId.value!!)
                deleteTodoState.value = DeleteTodoState.Success
            } catch (e: Exception) {
                deleteTodoState.value = DeleteTodoState.UnknownError
            }
        }
    }

    fun archiveTodo(displaySnackOnSuccess: Boolean) {
        viewModelScope.launch {
            try {
                todoRepository.archiveTodo(todoId.value!!)
                archiveTodoState.value = ArchiveTodoState.Success(displaySnackOnSuccess, todo.value!!.name)
            } catch (e: Exception) {
                archiveTodoState.value = ArchiveTodoState.UnknownError
            }
        }
    }

    fun unarchiveTodo(displaySnackOnSuccess: Boolean) {
        viewModelScope.launch {
            try {
                todoRepository.unarchiveTodo(todoId.value!!)
                unarchiveTodoState.value = UnarchiveTodoState.Success(displaySnackOnSuccess, todo.value!!.name)
            } catch (e: Exception) {
                unarchiveTodoState.value = UnarchiveTodoState.UnknownError
            }
        }
    }

    fun shouldShowArchiveMenu(): Boolean {
        val todo = todo.value
        return todo?.completed == true && !todo.archived
    }

    fun shouldShowUnarchiveMenu(): Boolean {
        val todo = todo.value
        return todo?.completed == true && todo.archived
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
    class Success(val displaySnack: Boolean, val todoName: String) : ArchiveTodoState()
    object UnknownError : ArchiveTodoState()
}

sealed class UnarchiveTodoState {
    class Success(val displaySnack: Boolean, val todoName: String) : UnarchiveTodoState()
    object UnknownError : UnarchiveTodoState()
}