package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object DailyXpUserTable: Table() {

    override val tableName: String
        get() = "daily_xp_user"

    val dailyXpId = reference("daily_xp_id", DailyXpTable.dailyXpId)
    val uid = reference("uid", UserTable.uid)
    val dayTaken = varchar("day_taken", 255).nullable()
    val isTaken = bool("is_taken").default(false)

}