package com.jbvincey.core.repositories

import androidx.lifecycle.LiveData
import com.jbvincey.core.database.TodoDao
import com.jbvincey.core.models.Todo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by jbvincey on 19/09/2018.
 */
interface TodoRepository {

    fun getAllTodos(): LiveData<List<Todo>>

    fun getAllArchivedTodos(): LiveData<List<Todo>>

    fun getAllUnarchivedTodos(): LiveData<List<Todo>>

    fun getTodoById(id: Long): LiveData<Todo>

    suspend fun addTodo(todoName: String)

    suspend fun editTodo(todoName: String, todoId: Long)

    suspend fun updateTodoCompleted(id: Long)

    suspend fun deleteTodo(id: Long)

    suspend  fun undeleteTodo(id: Long)

    suspend fun archiveTodo(id: Long)

    suspend fun unarchiveTodo(id: Long)

}

class TodoRepositoryImpl(private val todoDao: TodoDao): TodoRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getAllTodos(): LiveData<List<Todo>> = todoDao.getAllTodos()

    override fun getAllArchivedTodos(): LiveData<List<Todo>> = todoDao.getAllArchivedTodos()

    override fun getAllUnarchivedTodos(): LiveData<List<Todo>> = todoDao.getAllUnarchivedTodos()

    override fun getTodoById(id: Long): LiveData<Todo> = todoDao.getTodoById(id)

    override suspend fun addTodo(todoName: String) {
        withContext(ioDispatcher) {
            todoDao.insertTodo(Todo(todoName))
        }
    }

    override suspend fun editTodo(todoName: String, todoId: Long) {
        withContext(ioDispatcher) {
            todoDao.updateTodoName(todoName, todoId)
        }
    }

    override suspend fun updateTodoCompleted(id: Long) {
        withContext(ioDispatcher) {
            todoDao.updateTodoCompleted(id)
        }
    }

    override suspend fun deleteTodo(id: Long) {
        withContext(ioDispatcher) {
            todoDao.updateTodoDeleted(id, true)
        }
    }

    override suspend fun undeleteTodo(id: Long) {
        withContext(ioDispatcher) {
            todoDao.updateTodoDeleted(id, false)
        }
    }

    override suspend fun archiveTodo(id: Long) {
        withContext(ioDispatcher) {
            todoDao.updateTodoArchived(id, true)
        }
    }

    override suspend fun unarchiveTodo(id: Long) {
        withContext(ioDispatcher) {
            todoDao.updateTodoArchived(id, false)
        }
    }

}