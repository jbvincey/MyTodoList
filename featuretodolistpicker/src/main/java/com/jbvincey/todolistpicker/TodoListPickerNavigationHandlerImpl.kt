package com.jbvincey.todolistpicker

import android.content.Context
import com.jbvincey.navigation.TodoListPickerNavigationHandler

class TodoListPickerNavigationHandlerImpl: TodoListPickerNavigationHandler {

    override fun buildFeaturePath(context: Context, vararg parameters: Any) =
        context.resources.getString(R.string.feature_path_todolistpicker)
}