package com.jbvincey.todolist

import android.content.Context
import com.jbvincey.navigation.TodoListNavigationHandler

/**
 * Created by jbvincey on 09/10/2018.
 */
class TodoListNavigationHandlerImpl: TodoListNavigationHandler {

    override fun buildFeaturePath(context: Context, vararg parameters: Any): String {
        return context.resources.getString(R.string.feature_path_todolist)
    }

}