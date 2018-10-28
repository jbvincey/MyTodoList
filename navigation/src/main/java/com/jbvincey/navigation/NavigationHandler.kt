package com.jbvincey.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

/**
 * Created by jbvincey on 08/10/2018.
 */
interface NavigationHandler {

    fun buildTodoListIntent(context: Context): Intent

    fun buildAddTodoIntent(context: Context): Intent

    fun buildEditTodoIntent(context: Context, todoId: Long): Intent

}

class NavigationHandlerImpl(
        private val todoListNavigationHandler: TodoListNavigationHandler,
        private val addTodoNavigationHandler: AddTodoNavigationHandler,
        private val editTodoNavigationHandler: EditTodoNavigationHandler
): NavigationHandler {

    override fun buildTodoListIntent(context: Context): Intent {
        return buildIntent(
                todoListNavigationHandler.buildFeaturePath(context),
                todoListNavigationHandler.buildIntentParams(),
                context
        )
    }

    override fun buildAddTodoIntent(context: Context): Intent {
        return buildIntent(
                addTodoNavigationHandler.buildFeaturePath(context),
                addTodoNavigationHandler.buildIntentParams(),
                context
        )
    }

    override fun buildEditTodoIntent(context: Context, todoId: Long): Intent {
        return buildIntent(
                editTodoNavigationHandler.buildFeaturePath(context),
                editTodoNavigationHandler.buildIntentParams(todoId),
                context
        )
    }

    private fun buildIntent(featurePath: String, parameters: Bundle?, context: Context): Intent {
        val uri = Uri.Builder()
                .scheme(context.getString(R.string.app_scheme))
                .authority(context.getString(R.string.app_host))
                .path(featurePath)
                .build()

        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (parameters != null) {
            intent.putExtras(parameters)
        }

        return intent
    }

}