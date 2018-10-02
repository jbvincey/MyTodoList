package com.jbvincey.mytodolist

import android.app.Application
import com.facebook.stetho.Stetho
import com.jbvincey.core.dependencies.coreModule
import com.jbvincey.mytodolist.dependencies.appModule
import org.koin.android.ext.android.startKoin

/**
 * Created by jbvincey on 16/09/2018.
 */
class MyTodoListApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(appModule, coreModule))
        Stetho.initializeWithDefaults(this)
    }

}