package com.exraion.di

import com.exraion.routes.*
import org.koin.dsl.module

val routeModule = module {
    factory { AuthRoute(get(), get()) }
    factory { UserRoute(get(), get()) }
    factory { MenuRoute(get(), get()) }
    factory { VoucherRoute(get(), get()) }
    factory { LeaderboardRoute(get(), get()) }
}