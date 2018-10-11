package com.jbvincey.todolist

import com.jbvincey.navigation.TodoListNavigationHandler
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created by jbvincey on 03/10/2018.
 */
val featureTodoListModule = module {

    viewModel { TodoListArchViewModel(get(), get()) }

    factory { TodoTransformer() }

    factory { TodoListNavigationHandlerImpl() as TodoListNavigationHandler }

}