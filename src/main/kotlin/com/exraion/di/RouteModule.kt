package com.exraion.di

import com.exraion.routes.AuthRoute
import com.exraion.routes.UserRoute
import org.koin.dsl.module

val routeModule = module {
    factory { AuthRoute(get(), get()) }
    factory { UserRoute(get(), get()) }
}