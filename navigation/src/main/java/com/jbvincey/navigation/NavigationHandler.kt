package com.jbvincey.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.app.ActivityOptionsCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

/**
 * Created by jbvincey on 08/10/2018.
 */
interface NavigationHandler {

    fun goToAddTodoList(
        activity: Activity,
        addTodoListButton: View
    )

    fun goToTodoList(
        activity: Activity,
        todoListId: Long,
        @ColorRes backgroundColorRes: Int,
        todoListView: View
    )

    fun goToEditTodoList(
        activity: Activity,
        todoListId: Long,
        @ColorRes backgroundColorRes: Int,
        todoListView: View? = null
    )

    fun goToAddTodo(
        activity: Activity,
        todoListId: Long,
        @ColorRes backgroundColorRes: Int,
        addTodoButton: View
    )

    fun goToEditTodo(
        activity: Activity,
        todoId: Long,
        @ColorRes backgroundColorRes: Int,
        todoView: View
    )

    fun setupEnterTransition(
        activity: Activity,
        rootView: View,
        allContainerColors: Int? = null
    )

}

class NavigationHandlerImpl(
    private val addTodoListNavigationHandler: AddTodoListNavigationHandler,
    private val todoListNavigationHandler: TodoListNavigationHandler,
    private val editTodoListNavigationHandler: EditTodoListNavigationHandler,
    private val addTodoNavigationHandler: AddTodoNavigationHandler,
    private val editTodoNavigationHandler: EditTodoNavigationHandler
) : NavigationHandler {

    override fun goToAddTodoList(
        activity: Activity,
        addTodoListButton: View
    ) {
        val intent = buildIntent(
            featurePath = addTodoListNavigationHandler.buildFeaturePath(activity),
            parameters = addTodoListNavigationHandler.buildIntentParams(),
            context = activity
        )
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity,
            addTodoListButton,
            SHARE_TRANSITION_NAME
        )
        activity.startActivity(intent, options.toBundle())
    }

    override fun goToTodoList(
        activity: Activity,
        todoListId: Long,
        backgroundColorRes: Int,
        todoListView: View
    ) {
        val intent = buildIntent(
            featurePath = todoListNavigationHandler.buildFeaturePath(activity),
            parameters = todoListNavigationHandler.buildIntentParams(todoListId, backgroundColorRes),
            context = activity
        )
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity,
            todoListView,
            SHARE_TRANSITION_NAME
        )
        activity.startActivity(intent, options.toBundle())
    }

    override fun goToEditTodoList(
        activity: Activity,
        todoListId: Long,
        backgroundColorRes: Int,
        todoListView: View?
    ) {
        val intent = buildIntent(
            featurePath = editTodoListNavigationHandler.buildFeaturePath(activity),
            parameters = editTodoListNavigationHandler.buildIntentParams(todoListId, backgroundColorRes),
            context = activity
        )
        val options = if (todoListView != null) {
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                todoListView,
                SHARE_TRANSITION_NAME
            )
        } else {
            ActivityOptionsCompat.makeSceneTransitionAnimation(activity)
        }

        activity.startActivity(intent, options.toBundle())

    }

    override fun goToAddTodo(
        activity: Activity,
        todoListId: Long,
        backgroundColorRes: Int,
        addTodoButton: View
    ) {
        val intent = buildIntent(
            featurePath = addTodoNavigationHandler.buildFeaturePath(activity),
            parameters = addTodoNavigationHandler.buildIntentParams(todoListId, backgroundColorRes),
            context = activity
        )
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity,
            addTodoButton,
            SHARE_TRANSITION_NAME
        )
        activity.startActivity(intent, options.toBundle())
    }

    override fun goToEditTodo(
        activity: Activity,
        todoId: Long,
        backgroundColorRes: Int,
        todoView: View
    ) {
        val intent = buildIntent(
            featurePath = editTodoNavigationHandler.buildFeaturePath(activity),
            parameters = editTodoNavigationHandler.buildIntentParams(todoId, backgroundColorRes),
            context = activity
        )
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity,
            todoView,
            SHARE_TRANSITION_NAME
        )
        activity.startActivity(intent, options.toBundle())
    }

    override fun setupEnterTransition(
        activity: Activity,
        rootView: View,
        allContainerColors: Int?
    ) {
        activity.run {
            rootView.transitionName = SHARE_TRANSITION_NAME
            setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
            val containerTransform = MaterialContainerTransform().apply {
                addTarget(rootView)
                duration = SHARE_TRANSITION_DURATION
                interpolator = FastOutSlowInInterpolator()
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
            }
            window.sharedElementEnterTransition = containerTransform
            window.sharedElementReturnTransition = containerTransform
        }
    }
}

private fun buildIntent(featurePath: String, parameters: Bundle?, context: Context): Intent {
    val uri = Uri.Builder()
        .scheme(context.getString(R.string.app_scheme))
        .authority(context.getString(R.string.app_host))
        .path(featurePath)
        .build()

    return Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage(context.packageName)
        if (parameters != null) {
            putExtras(parameters)
        }
    }
}

private const val SHARE_TRANSITION_NAME = "shared_element_end_root"
private const val SHARE_TRANSITION_DURATION = 300L