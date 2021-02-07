package com.jbvincey.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorRes
import com.jbvincey.navigation.NavigationException
import com.jbvincey.navigation.TodoListNavigationHandler

/**
 * Created by jbvincey on 09/10/2018.
 */
class TodoListNavigationHandlerImpl: TodoListNavigationHandler {

    override fun buildFeaturePath(context: Context, vararg parameters: Any) =
        context.resources.getString(R.string.feature_path_todolist)

    override fun buildIntentParams(vararg parameters: Any): Bundle? {
        if (parameters.size != 2 || parameters[0] !is Long || parameters[1] !is Int) {
            throw NavigationException("missing todoList in Intent")
        }

        return buildIntentParams(parameters[0] as Long, parameters[1] as Int)
    }

    private fun buildIntentParams(todoListId: Long, @ColorRes backgroundColorRes: Int): Bundle? {
        val bundle = Bundle()
        bundle.putLong(KEY_TODOLIST_ID, todoListId)
        bundle.putInt(KEY_BACKGROUND_COLOR_RES, backgroundColorRes)
        return bundle
    }

    override fun retrieveTodoListId(intent: Intent): Long {
        val bundle = intent.extras ?: throw NavigationException("missing todolist Id in Intent")
        return bundle.getLong(KEY_TODOLIST_ID)
    }

    override fun retrieveBackgroundColorRes(intent: Intent): Int {
        val bundle = intent.extras ?: throw NavigationException("missing background color ressource in Intent")
        return bundle.getInt(KEY_BACKGROUND_COLOR_RES)
    }

}

private const val KEY_TODOLIST_ID = "KEY_TODOLIST_ID"
private const val KEY_BACKGROUND_COLOR_RES = "KEY_BACKGROUND_COLOR_RES"