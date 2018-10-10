package com.jbvincey.featureaddtodo

import android.content.Context
import com.jbvincey.navigation.AddTodoNavigationHandler

/**
 * Created by jbvincey on 09/10/2018.
 */
class AddTodoNavigationHandlerImpl: AddTodoNavigationHandler {

    override fun buildFeaturePath(context: Context, vararg parameters: Any): String {
        return context.resources.getString(R.string.feature_path_addtodo)
    }

}