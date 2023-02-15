package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object UserTable: Table() {

    override val tableName: String = "user"

    val uid = varchar("uid", 128)
    val email = varchar("email", 64)
    val password = varchar("password", 1024)
    val salt = varchar("salt", 1024)

    val location = varchar("location", 256).default("")
    val avatar = varchar("avatar", 512).default("")
    val beUPay = integer("beu_pay").default(0)
    val name = varchar("name", 64).default("")
    val phoneNumber = varchar("phone_number", 64).default("")
    val xp = integer("xp").default(0)

    override val primaryKey: PrimaryKey = PrimaryKey(uid)
}