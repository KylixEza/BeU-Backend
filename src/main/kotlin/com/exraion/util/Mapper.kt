package com.exraion.util

import com.exraion.data.tables.*
import com.exraion.model.history.HistoryResponse
import com.exraion.model.menu.MenuDetailResponse
import com.exraion.model.menu.MenuListResponse
import com.exraion.model.review.ReviewResponse
import com.exraion.model.user.User
import com.exraion.model.user.UserResponse
import com.exraion.model.voucher.VoucherDetailResponse
import com.exraion.model.voucher.VoucherListResponse
import org.jetbrains.exposed.sql.Avg
import org.jetbrains.exposed.sql.Count
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
    difficulty = this[MenuTable.difficulty],
    rangePrice = "Rp ${this[MenuTable.startPrice]} - Rp ${this[MenuTable.endPrice]}",
    rating = this[Avg(ReviewTable.rating, 1).alias("rating")] ?: BigDecimal.valueOf(0.0),
    cookTime = this[MenuTable.cookTime],
    isFavorite = this[FavoriteTable.uid] == uid
)

fun ResultRow.toReviewResponse() = ReviewResponse(
    name = this[UserTable.name],
    avatar = this[UserTable.avatar],
    rating = this[ReviewTable.rating],
)

fun ResultRow.toMenuDetailResponse(
    uid: String,
    ingredients: List<String>,
    tools: List<String>,
    steps: List<String>,
    reviews: List<ReviewResponse>
    ) = MenuDetailResponse(
    menuId = this[MenuTable.menuId],
    title = this[MenuTable.title],
    videoUrl = this[MenuTable.videoUrl],
    isFavorite = this[FavoriteTable.uid] == uid,
    isAvailable = this[MenuTable.isAvailable],
    ingredients = ingredients,
    tools = tools,
    steps = steps,
    description = this[MenuTable.description],
    estimatedTime = this[MenuTable.estimatedTime],
    benefit = this[MenuTable.benefit],
    reviewsCount = this[Count(ReviewTable.rating).alias("review_count")],
    averageRating = this[Avg(ReviewTable.rating, 1).alias("rating")] ?: BigDecimal.valueOf(0.0),
    reviews = reviews,
)

fun ResultRow.toHistoryResponse(
    ingredients: List<Pair<String, String>>
) = HistoryResponse(
    orderId = this[OrderTable.orderId],
    menuId = this[MenuTable.menuId],
    image = this[MenuTable.image],
    title = this[MenuTable.title],
    ingredients = ingredients.filter { it.first == this[OrderTable.orderId] }.map { it.second },
    timeStamp = this[OrderTable.timeStamp],
    status = this[OrderTable.status],
    starsGiven = this[OrderTable.starsGiven],
)

fun ResultRow.toVoucherListResponse() = VoucherListResponse(
    voucherId = this[VoucherTable.voucherId],
    category = this[VoucherTable.category],
    xpCost = this[VoucherTable.xpCost],
    validUntil = this[VoucherTable.validUntil],
    discount = this[VoucherTable.discount],
    minimumSpend = this[VoucherTable.minimumSpend],
    maximumDiscount = this[VoucherTable.maximumDiscount],
)

fun ResultRow.toVoucherDetailResponse() = VoucherDetailResponse(
    voucherId = this[VoucherTable.voucherId],
    category = this[VoucherTable.category],
    xpCost = this[VoucherTable.xpCost],
    validUntil = this[VoucherTable.validUntil],
    discount = this[VoucherTable.discount],
    minimumSpend = this[VoucherTable.minimumSpend],
    maximumDiscount = this[VoucherTable.maximumDiscount],
)
