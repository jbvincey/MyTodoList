package com.jbvincey.featureaddtodo.addtodo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jbvincey.core.repositories.TodoRepository
import kotlinx.coroutines.launch

/**
 * Created by jbvincey on 24/09/2018.
 */
class AddTodoArchViewModel(private val todoRepository: TodoRepository): ViewModel() {

    val addTodoState = MutableLiveData<AddTodoState>()

    fun addTodo(todoName: String) {
        viewModelScope.launch {
            try {
                todoRepository.addTodo(todoName)
                addTodoState.value = Success
            } catch (e: Exception) {
                addTodoState.value = UnknownError
            }
        }
    }
}

sealed class AddTodoState
object Success: AddTodoState()
object UnknownError: AddTodoState()