package com.jbvincey.mytodolist

import android.app.Application
import com.facebook.stetho.Stetho
import com.jbvincey.core.dependencies.coreModule
import com.jbvincey.featureaddtodo.featureAddTodoModule
import com.jbvincey.todolist.featureTodoListModule
import org.koin.android.ext.android.startKoin

/**
 * Created by jbvincey on 16/09/2018.
 */
class MyTodoListApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(
                coreModule,
                featureTodoListModule,
                featureAddTodoModule
        ))

        Stetho.initializeWithDefaults(this)
    }

}