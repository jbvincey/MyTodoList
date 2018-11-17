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

    fun getAllArchivedTodos(): LiveData<List<Todo>>

    fun getAllUnarchivedTodos(): LiveData<List<Todo>>

    fun getTodoById(id: Long): LiveData<Todo>

    fun addTodo(todoName: String): Completable

    fun editTodo(todoName: String, todoId: Long): Completable

    fun updateTodoCompleted(id: Long): Completable

    fun deleteTodo(id: Long): Completable

    fun undeleteTodo(id: Long): Completable

    fun archiveTodo(id: Long): Completable

    fun unarchiveTodo(id: Long): Completable

}

class TodoRepositoryImpl(private val todoDao: TodoDao): TodoRepository {

    override fun getAllTodos(): LiveData<List<Todo>> = todoDao.getAllTodos()

    override fun getAllArchivedTodos(): LiveData<List<Todo>> = todoDao.getAllArchivedTodos()

    override fun getAllUnarchivedTodos(): LiveData<List<Todo>> = todoDao.getAllUnarchivedTodos()

    override fun getTodoById(id: Long): LiveData<Todo> = todoDao.getTodoById(id)

    override fun addTodo(todoName: String): Completable {
        return Completable.create {
            todoDao.insertTodo(Todo(todoName))
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun editTodo(todoName: String, todoId: Long): Completable {
        return Completable.create {
            todoDao.updateTodoName(todoName, todoId)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun updateTodoCompleted(id: Long): Completable {
        return Completable.create {
            todoDao.updateTodoCompleted(id)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteTodo(id: Long): Completable {
        return Completable.create {
            todoDao.updateTodoDeleted(id, true)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun undeleteTodo(id: Long): Completable {
        return Completable.create {
            todoDao.updateTodoDeleted(id, false)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun archiveTodo(id: Long): Completable {
        return Completable.create {
            todoDao.updateTodoArchived(id, true)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    override fun unarchiveTodo(id: Long): Completable {
        return Completable.create {
            todoDao.updateTodoArchived(id, false)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
    }

}