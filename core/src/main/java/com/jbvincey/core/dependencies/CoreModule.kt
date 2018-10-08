package com.jbvincey.core.dependencies

import android.arch.persistence.room.Room
import com.jbvincey.core.database.MyTodoListDb
import com.jbvincey.core.navigation.NavigationHandler
import com.jbvincey.core.navigation.NavigationHandlerImpl
import com.jbvincey.core.repositories.TodoRepository
import com.jbvincey.core.repositories.TodoRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

/**
 * Created by jbvincey on 01/10/2018.
 */

val coreModule = module {

    single { TodoRepositoryImpl(get()) as TodoRepository }

    single {
        Room.databaseBuilder(androidContext(), MyTodoListDb::class.java, "mytodolist.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    single { get<MyTodoListDb>().getTodoDao() }

    factory { NavigationHandlerImpl() as NavigationHandler }

}