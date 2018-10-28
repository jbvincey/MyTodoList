package com.jbvincey.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Created by jbvincey on 05/10/2018.
 */
interface FeatureNavigationHandler {

    fun buildFeaturePath(context: Context, vararg parameters: Any): String

    fun buildIntentParams(vararg parameters: Any): Bundle? {
        return null
    }

}

interface TodoListNavigationHandler: FeatureNavigationHandler

interface AddTodoNavigationHandler: FeatureNavigationHandler

interface EditTodoNavigationHandler: FeatureNavigationHandler {
    fun retrieveTodoId(intent: Intent): Long
}