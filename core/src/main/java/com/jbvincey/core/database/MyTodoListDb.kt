package com.jbvincey.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jbvincey.core.models.TodoList
import com.jbvincey.core.models.Todo

/**
 * Created by jbvincey on 16/09/2018.
 */
@Database(
        entities = [
            TodoList::class,
            Todo::class
        ],
        version = 2,
        exportSchema = false
)
@TypeConverters(MyTodoListTypeConverters::class)
abstract class MyTodoListDb: RoomDatabase() {

    abstract fun getTodoListDao(): TodoListDao
    abstract fun getTodoDao(): TodoDao

}