package com.exraion.data.repositories.user

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.exraion.data.database.DatabaseFactory
import com.exraion.data.tables.FavoriteTable
import com.exraion.data.tables.ReviewTable
import com.exraion.data.tables.UserTable
import com.exraion.model.auth.RegisterBody
import com.exraion.model.review.ReviewBody
import com.exraion.model.user.User
import com.exraion.model.user.UserBody
import com.exraion.model.user.UserResponse
import com.exraion.util.toUser
import com.exraion.util.toUserResponse
import com.exraion.security.hashing.SaltedHash
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

    override suspend fun insertReview(uid: String, body: ReviewBody): Unit = dbFactory.dbQuery {
        ReviewTable.insert {
            it[reviewId] = "REVIEW${NanoIdUtils.randomNanoId()}"
            it[menuId] = body.menuId
            it[ReviewTable.uid] = uid
            it[rating] = body.rating
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

    override suspend fun updateUserAvatar(uid: String, avatar: String): Unit = dbFactory.dbQuery {
        UserTable.update(
            where = {UserTable.uid.eq(uid)}
        ) { table ->
            table[UserTable.avatar] = avatar
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
}