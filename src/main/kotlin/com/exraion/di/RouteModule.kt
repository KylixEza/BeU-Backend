package com.exraion.di

import com.exraion.routes.AuthRoute
import com.exraion.routes.MenuRoute
import com.exraion.routes.UserRoute
import com.exraion.routes.VoucherRoute
import org.koin.dsl.module

val routeModule = module {
    factory { AuthRoute(get(), get()) }
    factory { UserRoute(get(), get()) }
    factory { MenuRoute(get(), get()) }
    factory { VoucherRoute(get(), get()) }
}