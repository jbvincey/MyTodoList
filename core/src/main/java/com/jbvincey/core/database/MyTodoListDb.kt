package com.jbvincey.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jbvincey.core.models.Todo

/**
 * Created by jbvincey on 16/09/2018.
 */
@Database(
        entities = [
            Todo::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(MyTodoListTypeConverters::class)
abstract class MyTodoListDb: RoomDatabase() {

    abstract fun getTodoDao(): TodoDao

}