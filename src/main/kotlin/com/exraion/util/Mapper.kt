package com.exraion.util

import com.exraion.data.tables.UserTable
import com.exraion.model.user.User
import com.exraion.model.user.UserResponse
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser() = User(
    uid = this[UserTable.uid],
    email = this[UserTable.email],
    name = this[UserTable.name],
    password = this[UserTable.password],
    salt = this[UserTable.salt]
)

fun ResultRow.toUserResponse() = UserResponse(
    uid = this[UserTable.uid],
    location = this[UserTable.location],
    avatar = this[UserTable.avatar],
    beUPay = this[UserTable.beUPay],
    email = this[UserTable.email],
    name = this[UserTable.name],
    phoneNumber = this[UserTable.phoneNumber],
    xp = this[UserTable.xp]
)