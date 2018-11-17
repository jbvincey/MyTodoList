package com.jbvincey.core.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.jbvincey.core.models.Todo

/**
 * Created by jbvincey on 16/09/2018.
 */
@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodo(todo: Todo)

    @Delete
    fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM todos WHERE deleted = 0")
    fun getAllTodos(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE archived = 1 AND deleted = 0")
    fun getAllArchivedTodos(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE archived = 0 AND deleted = 0")
    fun getAllUnarchivedTodos(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :id")
    fun getTodoById(id: Long): LiveData<Todo>

    @Query("UPDATE todos Set completed = NOT completed WHERE id = :id")
    fun updateTodoCompleted(id: Long)

    @Query("UPDATE todos Set name = :name WHERE id = :id")
    fun updateTodoName(name: String, id: Long)

    @Query("UPDATE todos Set archived = :archive WHERE id = :id")
    fun updateTodoArchived(id: Long, archive: Boolean)

    @Query("UPDATE todos Set deleted = :delete WHERE id = :id")
    fun updateTodoDeleted(id: Long, delete: Boolean)

}