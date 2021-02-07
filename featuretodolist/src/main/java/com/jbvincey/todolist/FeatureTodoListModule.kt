package com.jbvincey.todolist

import com.jbvincey.navigation.TodoListNavigationHandler
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by jbvincey on 03/10/2018.
 */
val featureTodoListModule = module {

    viewModel { TodoListArchViewModel(
        todoRepository = get(),
        todoListRepository = get(),
        todoToCheckableCellViewModelTransformer = get()
    ) }

    factory { TodoToCheckableCellViewModelTransformer() }

    factory { TodoListNavigationHandlerImpl() as TodoListNavigationHandler }

}