package com.jbvincey.mytodolist.dependencies

import com.jbvincey.mytodolist.features.addtodo.AddTodoArchViewModel
import com.jbvincey.mytodolist.features.todolist.TodoListArchViewModel
import com.jbvincey.mytodolist.features.todolist.TodoViewModelTransformer
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created by jbvincey on 19/09/2018.
 */
val appModule = module {

    viewModel { TodoListArchViewModel(get(), get()) }

    viewModel { AddTodoArchViewModel(get()) }

    factory { TodoViewModelTransformer() }

}