package com.jbvincey.core.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
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