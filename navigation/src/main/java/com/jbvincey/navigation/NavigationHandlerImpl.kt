package com.jbvincey.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.StringRes

/**
 * Created by jbvincey on 08/10/2018.
 */
class NavigationHandlerImpl: NavigationHandler {

    override fun buildTodoListIntent(context: Context): Intent {
        return buildBaseIntent(R.string.feature_path_todolist, context)
    }

    override fun buildAddTodoIntent(context: Context): Intent {
        return buildBaseIntent(R.string.feature_path_addtodo, context)
    }

    private fun buildBaseIntent(@StringRes featurePathStringRes: Int, context: Context): Intent {
        val uri = Uri.Builder()
                .scheme(context.getString(R.string.app_scheme))
                .authority(context.getString(R.string.app_host))
                .path(context.getString(featurePathStringRes))
                .build()

        return Intent(Intent.ACTION_VIEW, uri)
    }

}