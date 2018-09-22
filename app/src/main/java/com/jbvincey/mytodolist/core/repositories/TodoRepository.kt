package com.jbvincey.mytodolist.core.repositories

import android.arch.lifecycle.LiveData
import com.jbvincey.mytodolist.core.database.TodoDao
import com.jbvincey.mytodolist.models.Todo

/**
 * Created by jbvincey on 19/09/2018.
 */
interface TodoRepository {

    fun getAllTodos(): LiveData<List<Todo>>

    fun getTodoById(id: Long): LiveData<Todo>

}

class TodoRepositoryImpl(private val todoDao: TodoDao): TodoRepository {

    override fun getAllTodos(): LiveData<List<Todo>> = todoDao.getAllTodos()

    override fun getTodoById(id: Long): LiveData<Todo> = todoDao.getTodoById(id)

}