package com.jbvincey.core.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jbvincey.core.models.TodoList
import com.jbvincey.core.models.TodoListWithTodos

@Dao
interface TodoListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodoList(todoList: TodoList)

    @Delete
    fun deleteTodoList(todoList: TodoList)

    @Query("SELECT * FROM todolists WHERE deleted = 0")
    fun getAllTodoLists(): LiveData<List<TodoList>>

    @Query("SELECT * FROM todolists WHERE deleted = 0")
    fun getAllTodoListsWithTodos(): LiveData<List<TodoListWithTodos>>

    @Query("SELECT * FROM todoLists WHERE id = :id")
    fun getTodoListById(id: Long): LiveData<TodoList>

    @Query("SELECT * FROM todoLists WHERE id = :id")
    fun getTodoListWithTodosById(id: Long): LiveData<TodoListWithTodos>

    @Query("UPDATE todoLists Set name = :name WHERE id = :id")
    fun updateTodoListName(name: String, id: Long)

    @Query("UPDATE todolists Set deleted = :delete WHERE id = :id")
    fun updateTodoListDeleted(id: Long, delete: Boolean)
}