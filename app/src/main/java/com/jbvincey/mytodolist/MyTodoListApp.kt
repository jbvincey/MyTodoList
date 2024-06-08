package com.jbvincey.mytodolist

import android.app.Application
import com.jbvincey.core.dependencies.coreModule
import com.jbvincey.featureaddtodo.featureAddTodoModule
import com.jbvincey.featureaddtodolist.featureAddListTodoModule
import com.jbvincey.navigation.navigationModule
import com.jbvincey.todolist.featureTodoListModule
import com.jbvincey.todolistpicker.featureTodoListPickerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Created by jbvincey on 16/09/2018.
 */
class MyTodoListApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger(Level.ERROR)

            // use the Android context given there
            androidContext(this@MyTodoListApp)

            // load properties from assets/koin.properties file
            androidFileProperties()

            // module list
            modules(listOf(
                coreModule,
                navigationModule,
                featureTodoListPickerModule,
                featureAddListTodoModule,
                featureTodoListModule,
                featureAddTodoModule
            ))
        }

        AppInitializer.initializeFlipper(this)
    }



}