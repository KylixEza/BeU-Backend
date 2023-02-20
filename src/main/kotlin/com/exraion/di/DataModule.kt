package com.exraion.di

import com.exraion.data.database.DatabaseFactory
import com.exraion.data.repositories.menu.MenuRepository
import com.exraion.data.repositories.menu.MenuRepositoryImpl
import com.exraion.data.repositories.token.TokenRepository
import com.exraion.data.repositories.token.TokenRepositoryImpl
import com.exraion.data.repositories.user.UserRepository
import com.exraion.data.repositories.user.UserRepositoryImpl
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.dsl.module
import java.net.URI

val databaseModule = module {
    single {
        DatabaseFactory(get())
    }

    factory {
        val config = HikariConfig()
        config.apply {
            driverClassName = System.getenv("JDBC_DRIVER")
            maximumPoolSize = 6
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            jdbcUrl = if(System.getenv("ENV") == "DEV") {
                System.getenv("DATABASE_URL")
            } else {
                val uri = URI(System.getenv("DATABASE_URL"))
                val username = uri.userInfo.split(":").toTypedArray()[0]
                val password = uri.userInfo.split(":").toTypedArray()[1]
                "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}?sslmode=require&user=$username&password=$password"
            }

            validate()
        }
        HikariDataSource(config)
    }
}

val repositoryModule = module {
    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    single<TokenRepository> {
        TokenRepositoryImpl(get())
    }

    single<MenuRepository> {
        MenuRepositoryImpl(get())
    }
}