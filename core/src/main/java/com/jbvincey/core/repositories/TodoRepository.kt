package com.jbvincey.core.repositories

import com.jbvincey.core.database.TodoDao
import com.jbvincey.core.models.Todo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Created by jbvincey on 19/09/2018.
 */
interface TodoRepository {

    fun getAllTodosFromTodoList(todoListId: Long): Flow<List<Todo>>

    fun getAllArchivedTodosFromTodoList(todoListId: Long): Flow<List<Todo>>

    fun getAllUnarchivedTodosFromTodoList(todoListId: Long): Flow<List<Todo>>

    fun getTodoById(id: Long): Flow<Todo>

    suspend fun addTodo(todoName: String, todoListId: Long)

    suspend fun editTodo(todoName: String, todoId: Long)

    suspend fun updateTodoCompleted(id: Long)

    suspend fun deleteTodo(id: Long)

    suspend  fun undeleteTodo(id: Long)

    suspend fun archiveTodo(id: Long)

    suspend fun unarchiveTodo(id: Long)

}

class TodoRepositoryImpl(private val todoDao: TodoDao): TodoRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getAllTodosFromTodoList(todoListId: Long): Flow<List<Todo>> = todoDao.getAllTodosFromTodoList(todoListId)

    override fun getAllArchivedTodosFromTodoList(todoListId: Long): Flow<List<Todo>> = todoDao.getAllArchivedTodosFromTodoList(todoListId)

    override fun getAllUnarchivedTodosFromTodoList(todoListId: Long): Flow<List<Todo>> = todoDao.getAllUnarchivedTodosFromTodoList(todoListId)

    override fun getTodoById(id: Long): Flow<Todo> = todoDao.getTodoById(id)

    override suspend fun addTodo(todoName: String, todoListId: Long) {
        withContext(ioDispatcher) {
            todoDao.insertTodo(Todo(todoName, todoListId))
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