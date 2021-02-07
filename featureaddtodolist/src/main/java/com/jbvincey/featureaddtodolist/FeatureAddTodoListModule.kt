package com.jbvincey.featureaddtodolist

import com.jbvincey.featureaddtodolist.add.AddTodoListArchViewModel
import com.jbvincey.featureaddtodolist.add.AddTodoListNavigationHandlerImpl
import com.jbvincey.featureaddtodolist.edit.EditTodoListArchViewModel
import com.jbvincey.featureaddtodolist.edit.EditTodoListNavigationHandlerImpl
import com.jbvincey.navigation.AddTodoListNavigationHandler
import com.jbvincey.navigation.EditTodoListNavigationHandler
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureAddListTodoModule = module {

    viewModel { AddTodoListArchViewModel(get()) }

    factory { AddTodoListNavigationHandlerImpl() as AddTodoListNavigationHandler }

    viewModel { EditTodoListArchViewModel(get()) }

    factory { EditTodoListNavigationHandlerImpl() as EditTodoListNavigationHandler }
}