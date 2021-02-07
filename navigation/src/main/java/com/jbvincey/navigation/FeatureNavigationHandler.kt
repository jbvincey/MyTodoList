package com.jbvincey.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorRes

/**
 * Created by jbvincey on 05/10/2018.
 */
interface FeatureNavigationHandler {

    fun buildFeaturePath(context: Context, vararg parameters: Any): String

    fun buildIntentParams(vararg parameters: Any): Bundle? {
        return null
    }

}

interface FeatureWithBackgroundColorNavigationHandler: FeatureNavigationHandler {
    @ColorRes
    fun retrieveBackgroundColorRes(intent: Intent): Int
}

interface AddTodoListNavigationHandler: FeatureNavigationHandler

interface EditTodoListNavigationHandler: FeatureWithBackgroundColorNavigationHandler {
    fun retrieveTodoListId(intent: Intent): Long
}

interface TodoListPickerNavigationHandler: FeatureNavigationHandler

interface TodoListNavigationHandler: FeatureWithBackgroundColorNavigationHandler {
    fun retrieveTodoListId(intent: Intent): Long
}

interface AddTodoNavigationHandler: FeatureWithBackgroundColorNavigationHandler {
    fun retrieveTodoListId(intent: Intent): Long
}

interface EditTodoNavigationHandler: FeatureWithBackgroundColorNavigationHandler {
    fun retrieveTodoId(intent: Intent): Long
}