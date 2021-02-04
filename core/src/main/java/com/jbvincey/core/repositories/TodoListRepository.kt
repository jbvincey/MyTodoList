package com.jbvincey.core.repositories

import androidx.lifecycle.LiveData
import com.jbvincey.core.database.TodoListDao
import com.jbvincey.core.models.TodoList
import com.jbvincey.core.models.TodoListWithTodos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface TodoListRepository {

    fun getAllTodoLists(): LiveData<List<TodoList>>

    fun getAllTodoListsWithTodos(): LiveData<List<TodoListWithTodos>>

    fun getTodoListById(id: Long): LiveData<TodoList>

    suspend fun addTodoList(todoListName: String)

    suspend fun editTodoList(todoListName: String, todoListId: Long)

    suspend fun deleteTodoList(todoListId: Long)

    suspend fun undeleteTodoList(todoListId: Long)
}

class TodoListRepositoryImpl(private val todoListDao: TodoListDao): TodoListRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getAllTodoLists(): LiveData<List<TodoList>> = todoListDao.getAllTodoLists()

    override fun getAllTodoListsWithTodos(): LiveData<List<TodoListWithTodos>> = todoListDao.getAllTodoListsWithTodos()

    override fun getTodoListById(id: Long): LiveData<TodoList> = todoListDao.getTodoListById(id)

    override suspend fun addTodoList(todoListName: String) {
        withContext(ioDispatcher) {
            todoListDao.insertTodoList(TodoList(todoListName))
        }
    }

    override suspend fun editTodoList(todoListName: String, todoListId: Long) {
        withContext(ioDispatcher) {
            todoListDao.updateTodoListName(todoListName, todoListId)
        }
    }

    override suspend fun deleteTodoList(todoListId: Long) {
        withContext(ioDispatcher) {
            todoListDao.updateTodoListDeleted(todoListId, true)
        }
    }

    override suspend fun undeleteTodoList(todoListId: Long) {
        withContext(ioDispatcher) {
            todoListDao.updateTodoListDeleted(todoListId, false)
        }
    }
}