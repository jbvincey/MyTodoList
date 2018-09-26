package com.jbvincey.mytodolist.features.addtodo

import android.arch.lifecycle.ViewModel
import com.jbvincey.mytodolist.core.repositories.TodoRepository

/**
 * Created by jbvincey on 24/09/2018.
 */
class AddTodoViewModel(private val todoRepository: TodoRepository): ViewModel() {

    fun addTodo(todoName: String) = todoRepository.addTodo(todoName)

}