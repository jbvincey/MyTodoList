package com.jbvincey.navigation

import org.koin.dsl.module

/**
 * Created by jbvincey on 09/10/2018.
 */

val navigationModule = module {

    factory { NavigationHandlerImpl(get(), get(), get()) as NavigationHandler }

}