package com.jbvincey.navigation

import android.content.Context
import android.os.Bundle

/**
 * Created by jbvincey on 05/10/2018.
 */
interface FeatureNavigationHandler {

    fun buildFeaturePath(context: Context, vararg parameters: Any): String

    fun buildIntentParams(): Bundle? {
        return null
    }

}

interface AddTodoNavigationHandler: FeatureNavigationHandler

interface TodoListNavigationHandler: FeatureNavigationHandler