package com.jbvincey.featureaddtodo

import android.arch.lifecycle.ViewModel
import com.jbvincey.core.repositories.TodoRepository

/**
 * Created by jbvincey on 24/09/2018.
 */
class AddTodoArchViewModel(private val todoRepository: TodoRepository): ViewModel() {

    fun addTodo(todoName: String) = todoRepository.addTodo(todoName)

}