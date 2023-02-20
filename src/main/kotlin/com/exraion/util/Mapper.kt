package com.exraion.util

import com.exraion.data.tables.FavoriteTable
import com.exraion.data.tables.MenuTable
import com.exraion.data.tables.ReviewTable
import com.exraion.data.tables.UserTable
import com.exraion.model.menu.MenuListResponse
import com.exraion.model.user.User
import com.exraion.model.user.UserResponse
import org.jetbrains.exposed.sql.Avg
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.alias
import java.math.BigDecimal

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

fun ResultRow.toMenuListResponse(uid: String) = MenuListResponse(
    menuId = this[MenuTable.menuId],
    image = this[MenuTable.image],
    title = this[MenuTable.title],
    rangePrice = "Rp ${this[MenuTable.startPrice]} - Rp ${this[MenuTable.endPrice]}",
    rating = this[Avg(ReviewTable.rating, 1).alias("rating")] ?: BigDecimal.valueOf(0.0),
    cookTime = this[MenuTable.cookTime],
    isFavorite = this[FavoriteTable.uid] == uid
)