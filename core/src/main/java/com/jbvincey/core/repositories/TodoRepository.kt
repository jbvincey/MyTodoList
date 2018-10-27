package com.jbvincey.core.repositories

import android.arch.lifecycle.LiveData
import com.jbvincey.core.database.TodoDao
import com.jbvincey.core.models.Todo
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

/**
 * Created by jbvincey on 19/09/2018.
 */
interface TodoRepository {

    fun getAllTodos(): LiveData<List<Todo>>

    fun getTodoById(id: Long): LiveData<Todo>

    fun addTodo(todoName: String): Completable

    fun changeTodoCompleted(id: Long): Completable

}

class TodoRepositoryImpl(private val todoDao: TodoDao): TodoRepository {

    override fun getAllTodos(): LiveData<List<Todo>> = todoDao.getAllTodos()

    override fun getTodoById(id: Long): LiveData<Todo> = todoDao.getTodoById(id)

    override fun addTodo(todoName: String): Completable {
        return Completable.create {
            todoDao.insertTodo(Todo(todoName))
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun changeTodoCompleted(id: Long): Completable {
        return Completable.create {
            todoDao.changeTodoCompleted(id)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

}