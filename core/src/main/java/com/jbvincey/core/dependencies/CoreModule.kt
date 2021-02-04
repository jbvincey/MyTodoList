package com.jbvincey.core.dependencies

import androidx.room.Room
import com.jbvincey.core.database.MyTodoListDb
import com.jbvincey.core.repositories.TodoListRepository
import com.jbvincey.core.repositories.TodoListRepositoryImpl
import com.jbvincey.core.repositories.TodoRepository
import com.jbvincey.core.repositories.TodoRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


/**
 * Created by jbvincey on 01/10/2018.
 */

val coreModule = module {

    single { TodoRepositoryImpl(get()) as TodoRepository }

    single { TodoListRepositoryImpl(get()) as TodoListRepository }

    single {
        Room.databaseBuilder(androidContext(), MyTodoListDb::class.java, "mytodolist.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    single { get<MyTodoListDb>().getTodoDao() }

    single { get<MyTodoListDb>().getTodoListDao() }

}