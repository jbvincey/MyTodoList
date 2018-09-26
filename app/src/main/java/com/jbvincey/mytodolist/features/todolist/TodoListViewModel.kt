package com.jbvincey.mytodolist.features.todolist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.jbvincey.mytodolist.core.repositories.TodoRepository
import com.jbvincey.mytodolist.models.Todo

/**
 * Created by jbvincey on 19/09/2018.
 */
class TodoListViewModel(todoRepository: TodoRepository): ViewModel() {

        val todoList: LiveData<List<Todo>> = todoRepository.getAllTodos()

}

