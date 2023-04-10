package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object DailyXpTable: Table() {

    override val tableName: String
        get() = "daily_xp"

    val dailyXpId = varchar("daily_xp_id", 255)
    val dailyXp = integer("daily_xp")
    val day = integer("day")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(dailyXpId)
}