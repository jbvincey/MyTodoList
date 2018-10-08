package com.jbvincey.core.navigation

import android.content.Context
import android.content.Intent

/**
 * Created by jbvincey on 05/10/2018.
 */
interface NavigationHandler {

    fun buildTodoListIntent(context: Context): Intent

    fun buildAddTodoIntent(context: Context): Intent

}