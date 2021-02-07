package com.jbvincey.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.ColorRes

/**
 * Created by jbvincey on 08/10/2018.
 */
interface NavigationHandler {

    fun buildAddTodoListIntent(context: Context): Intent

    fun buildTodoListIntent(context: Context, todoListId: Long, @ColorRes backgroundColorRes: Int): Intent

    fun buildEditTodoListIntent(context: Context, todoListId: Long, @ColorRes backgroundColorRes: Int): Intent

    fun buildAddTodoIntent(context: Context, todoListId: Long, @ColorRes backgroundColorRes: Int): Intent

    fun buildEditTodoIntent(context: Context, todoId: Long, @ColorRes backgroundColorRes: Int): Intent

}

class NavigationHandlerImpl(
    private val addTodoListNavigationHandler: AddTodoListNavigationHandler,
    private val todoListNavigationHandler: TodoListNavigationHandler,
    private val editTodoListNavigationHandler: EditTodoListNavigationHandler,
    private val addTodoNavigationHandler: AddTodoNavigationHandler,
    private val editTodoNavigationHandler: EditTodoNavigationHandler
) : NavigationHandler {

    override fun buildAddTodoListIntent(
        context: Context
    ): Intent =
        buildIntent(
            addTodoListNavigationHandler.buildFeaturePath(context),
            addTodoListNavigationHandler.buildIntentParams(),
            context
        )

    override fun buildTodoListIntent(
        context: Context,
        todoListId: Long,
        @ColorRes backgroundColorRes: Int
    ): Intent =
        buildIntent(
            todoListNavigationHandler.buildFeaturePath(context),
            todoListNavigationHandler.buildIntentParams(todoListId, backgroundColorRes),
            context
        )

    override fun buildEditTodoListIntent(
        context: Context,
        todoListId: Long,
        @ColorRes backgroundColorRes: Int
    ): Intent =
        buildIntent(
            editTodoListNavigationHandler.buildFeaturePath(context),
            editTodoListNavigationHandler.buildIntentParams(todoListId, backgroundColorRes),
            context
        )
    
    override fun buildAddTodoIntent(
        context: Context,
        todoListId: Long,
        @ColorRes backgroundColorRes: Int
    ): Intent =
        buildIntent(
            addTodoNavigationHandler.buildFeaturePath(context),
            addTodoNavigationHandler.buildIntentParams(todoListId, backgroundColorRes),
            context
        )

    override fun buildEditTodoIntent(
        context: Context,
        todoId: Long,
        @ColorRes backgroundColorRes: Int
    ): Intent =
        buildIntent(
            editTodoNavigationHandler.buildFeaturePath(context),
            editTodoNavigationHandler.buildIntentParams(todoId, backgroundColorRes),
            context
        )

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