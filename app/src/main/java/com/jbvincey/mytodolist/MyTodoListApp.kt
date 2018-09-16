package com.jbvincey.mytodolist

import android.app.Application
import com.facebook.stetho.Stetho

/**
 * Created by jbvincey on 16/09/2018.
 */
class MyTodoListApp: Application() {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
    }

}