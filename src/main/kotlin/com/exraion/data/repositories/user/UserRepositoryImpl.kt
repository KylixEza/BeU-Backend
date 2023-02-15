package com.exraion.data.repositories.user

import com.exraion.data.database.DatabaseFactory
import com.exraion.data.tables.UserTable
import com.exraion.model.auth.RegisterBody
import com.exraion.model.user.User
import com.exraion.model.user.UserBody
import com.exraion.model.user.UserResponse
import com.exraion.util.toUser
import com.exraion.util.toUserResponse
import com.oreyo.security.hashing.SaltedHash
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
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
}