package com.exraion.data.repositories.user

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.exraion.data.database.DatabaseFactory
import com.exraion.data.tables.*
import com.exraion.model.auth.RegisterBody
import com.exraion.model.history.HistoryResponse
import com.exraion.model.order.OrderBody
import com.exraion.model.user.User
import com.exraion.model.user.UserBody
import com.exraion.model.user.UserResponse
import com.exraion.security.hashing.SaltedHash
import com.exraion.util.*
import com.kylix.FirebaseStorageImage.uploadImage
import com.kylix.ImageExtension
import io.ktor.http.content.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class UserRepositoryImpl(
    private val dbFactory: DatabaseFactory
): UserRepository {
    override suspend fun insertUser(body: RegisterBody, saltedHash: SaltedHash): User {
        val uuid = UUID.randomUUID().toString()

        dbFactory.dbQuery {
            UserTable.insert { table ->
                table[uid] = uuid
                table[email] = body.email
                table[password] = saltedHash.hash
                table[salt] = saltedHash.salt
                table[location] = body.location
                table[avatar] = ""
                table[beUPay] = 0
                table[name] = body.name
                table[phoneNumber] = body.phoneNumber
                table[xp] = 0
            }
        }

        return User(
            uid = uuid,
            email = body.email,
            name = body.name,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
    }

    override suspend fun insertOrder(uid: String, body: OrderBody): Unit = dbFactory.dbQuery {

        val orderId = "ORDER${NanoIdUtils.randomNanoId()}"

        val dateObj = Date()
        val df: DateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm")
        df.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val dateCreated = df.format(dateObj)

        OrderTable.insert {
            it[OrderTable.orderId] = orderId
            it[menuId] = body.menuId
            it[OrderTable.uid] = uid
            it[timeStamp] = dateCreated
            it[status] = OrderStatus.PROCESSED.status
            it[starsGiven] = 0.0
            it[totalPrice] = body.totalPrice
        }

        body.ingredients.forEach { ingredient ->
            OrderIngredientTable.insert {
                it[OrderIngredientTable.orderId] = orderId
                it[OrderIngredientTable.ingredient] = ingredient
            }
        }
    }

    override suspend fun getUserByEmail(email: String): User = dbFactory.dbQuery {
        UserTable.select {
            UserTable.email.eq(email)
        }.map {
            it.toUser()
        }
    }.first()

    override suspend fun getDetailUser(uid: String): UserResponse = dbFactory.dbQuery {
        UserTable.select {
            UserTable.uid.eq(uid)
        }.mapNotNull {
            it.toUserResponse()
        }
    }.first()

    override suspend fun updateUser(uid: String, body: UserBody) {
        dbFactory.dbQuery {
            UserTable.update(
                where = {UserTable.uid.eq(uid)}
            ) { table ->
                table[location] = body.location
                table[beUPay] = body.beUPay
                table[email] = body.email
                table[name] = body.name
                table[phoneNumber] = body.phoneNumber
                table[xp] = body.xp
            }
        }
    }

    override suspend fun updateUserAvatar(uid: String, part: PartData.FileItem): Unit = dbFactory.dbQuery {
        val url = part.uploadImage(
            "avatar_path/$uid",
            ImageExtension.JPG
        ) { it.compress(0.4f) }

        UserTable.update(
            where = { UserTable.uid.eq(uid) }
        ) { table ->
            table[avatar] = url
        }
    }

    override suspend fun isEmailExist(email: String): Boolean = dbFactory.dbQuery {
        UserTable.select {
            UserTable.email.eq(email)
        }.mapNotNull {
            it[UserTable.email]
        }
    }.isNotEmpty()

    override suspend fun insertFavorite(uid: String, menuId: String) {
        dbFactory.dbQuery {
            FavoriteTable.insert { table ->
                table[FavoriteTable.uid] = uid
                table[FavoriteTable.menuId] = menuId
            }
        }
    }

    override suspend fun deleteFavorite(uid: String, menuId: String) {
        dbFactory.dbQuery {
            FavoriteTable.deleteWhere {
                (FavoriteTable.uid eq uid) and (FavoriteTable.menuId eq menuId)
            }
        }
    }

    override suspend fun getOrderHistory(uid: String): List<HistoryResponse> = dbFactory.dbQuery {

        val pairOrderIdAndIngredient = OrderTable.join(OrderIngredientTable, JoinType.INNER) {
            OrderTable.orderId eq OrderIngredientTable.orderId
        }.select { OrderTable.uid eq uid }.mapNotNull {
            Pair(it[OrderTable.orderId], it[OrderIngredientTable.ingredient])
        }

        OrderTable.join(MenuTable, JoinType.INNER) {
            OrderTable.menuId eq MenuTable.menuId
        }.select {
            OrderTable.uid.eq(uid)
        }.mapNotNull {
            it.toHistoryResponse(pairOrderIdAndIngredient)
        }
    }

    override suspend fun cancelOrder(orderId: String): Unit = dbFactory.dbQuery {
        OrderTable.update(
            where = { (OrderTable.orderId eq orderId) }
        ) { table ->
            table[status] = OrderStatus.CANCELLED.status
        }
    }

    override suspend fun updateOrderStars(uid: String, orderId: String, stars: Double): Unit = dbFactory.dbQuery {
        OrderTable.update(
            where = { (OrderTable.orderId eq orderId) }
        ) { table ->
            table[starsGiven] = stars
        }

        val menuId = OrderTable.select {
            OrderTable.orderId.eq(orderId)
        }.firstNotNullOf {
            it[OrderTable.menuId]
        }

        ReviewTable.insert {
            it[reviewId] = "REVIEW${NanoIdUtils.randomNanoId()}"
            it[ReviewTable.uid] = uid
            it[ReviewTable.menuId] = menuId
            it[rating] = stars
        }
    }
}