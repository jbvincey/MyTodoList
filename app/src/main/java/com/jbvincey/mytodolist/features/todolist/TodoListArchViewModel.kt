package com.jbvincey.mytodolist.features.todolist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.jbvincey.mytodolist.core.repositories.TodoRepository
import com.jbvincey.mytodolist.core.models.Todo
import com.jbvincey.ui.recycler.cells.todo.TodoViewModel

/**
 * Created by jbvincey on 19/09/2018.
 */
class TodoListArchViewModel(todoRepository: TodoRepository, todoViewModelTransformer: TodoViewModelTransformer): ViewModel() {

        private val todoList: LiveData<List<Todo>> = todoRepository.getAllTodos()

        val todoViewModelList: LiveData<List<TodoViewModel>> = Transformations.map(todoList) {
                todoViewModelTransformer.transform(it)
        }

}

