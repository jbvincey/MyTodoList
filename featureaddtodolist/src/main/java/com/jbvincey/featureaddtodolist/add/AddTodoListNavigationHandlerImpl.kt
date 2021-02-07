package com.jbvincey.featureaddtodolist.add

import android.content.Context
import android.os.Bundle
import com.jbvincey.navigation.AddTodoListNavigationHandler
import deezer.android.featureaddtodolist.R

class AddTodoListNavigationHandlerImpl: AddTodoListNavigationHandler {

    override fun buildFeaturePath(context: Context, vararg parameters: Any) =
        context.resources.getString(R.string.feature_path_addtodolist)

    override fun buildIntentParams(vararg parameters: Any): Bundle? = null

}