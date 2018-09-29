package com.jbvincey.mytodolist.dependencies

import android.arch.persistence.room.Room
import com.jbvincey.mytodolist.core.database.MyTodoListDb
import com.jbvincey.mytodolist.core.repositories.TodoRepository
import com.jbvincey.mytodolist.core.repositories.TodoRepositoryImpl
import com.jbvincey.mytodolist.features.addtodo.AddTodoArchViewModel
import com.jbvincey.mytodolist.features.todolist.TodoListArchViewModel
import com.jbvincey.mytodolist.features.todolist.TodoViewModelTransformer
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created by jbvincey on 19/09/2018.
 */
val appModule = module {

    viewModel { TodoListArchViewModel(get(), get()) }

    viewModel { AddTodoArchViewModel(get()) }

    single { TodoRepositoryImpl(get()) as TodoRepository }

    factory { TodoViewModelTransformer() }

    // MyTodoListDb
    single {
        Room.databaseBuilder(androidContext(), MyTodoListDb::class.java, "mytodolist.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    // TodoDao
    single { get<MyTodoListDb>().getTodoDao() }

}