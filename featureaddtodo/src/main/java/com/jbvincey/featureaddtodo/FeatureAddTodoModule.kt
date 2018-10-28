package com.jbvincey.featureaddtodo

import com.jbvincey.featureaddtodo.addtodo.AddTodoArchViewModel
import com.jbvincey.featureaddtodo.addtodo.AddTodoNavigationHandlerImpl
import com.jbvincey.featureaddtodo.edittodo.EditTodoArchViewModel
import com.jbvincey.featureaddtodo.edittodo.EditTodoNavigationHandlerImpl
import com.jbvincey.navigation.AddTodoNavigationHandler
import com.jbvincey.navigation.EditTodoNavigationHandler
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Created by jbvincey on 19/09/2018.
 */
val featureAddTodoModule = module {

    viewModel { AddTodoArchViewModel(get()) }

    factory { AddTodoNavigationHandlerImpl() as AddTodoNavigationHandler }

    viewModel { EditTodoArchViewModel(get()) }

    factory { EditTodoNavigationHandlerImpl() as EditTodoNavigationHandler }

}