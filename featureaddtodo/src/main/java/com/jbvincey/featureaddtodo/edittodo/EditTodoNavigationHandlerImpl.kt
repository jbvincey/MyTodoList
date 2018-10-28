package com.jbvincey.featureaddtodo.edittodo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jbvincey.featureaddtodo.R
import com.jbvincey.navigation.EditTodoNavigationHandler
import com.jbvincey.navigation.NavigationException

/**
 * Created by jbvincey on 09/10/2018.
 */
class EditTodoNavigationHandlerImpl: EditTodoNavigationHandler {

    companion object {
        const val KEY_TODO_ID = "KEY_TODO_ID"
        const val EXCEPTION_MISSING_TODO_ID_MESSAGE = ""
    }

    override fun buildFeaturePath(context: Context, vararg parameters: Any): String {
        return context.resources.getString(R.string.feature_path_edittodo)
    }

    override fun buildIntentParams(vararg parameters: Any): Bundle? {
        if (parameters.size != 1 || parameters[0] !is Long) {
            throw NavigationException(EXCEPTION_MISSING_TODO_ID_MESSAGE)
        }

        return buildIntentParams(parameters[0] as Long)
    }

    private fun buildIntentParams(todoId: Long): Bundle? {
        val bundle = Bundle()
        bundle.putLong(KEY_TODO_ID, todoId)
        return bundle
    }

    override fun retrieveTodoId(intent: Intent): Long {
        val bundle = intent.extras ?: throw NavigationException(EXCEPTION_MISSING_TODO_ID_MESSAGE)
        return bundle.getLong(KEY_TODO_ID)
    }
}