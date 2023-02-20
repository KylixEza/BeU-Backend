package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object MenuTable: Table() {

    override val tableName: String = "menu"

    val menuId = varchar("menu_id", 128)
    val benefit = varchar("benefit", 1024).default("")
    val category = varchar("type", 36)
    val description = varchar("description", 1024).default("")
    val difficulty = varchar("difficulty", 24)
    val calories = integer("calories").default(0)
    val cookTime = integer("cook_time").default(0)
    val estimatedTime = varchar("estimated_time", 36).default("")
    val image = varchar("image", 256).default("")
    val ordered = integer("ordered").default(0)
    val startPrice = integer("start_price").default(0)
    val endPrice = integer("end_price").default(0)
    val title = varchar("title", 128)
    val videoUrl = varchar("video_url", 256).default("")
    val xpGained = integer("xp").default(0)
    val isExclusive = bool("is_exclusive").default(false)
    val isAvailable = bool("is_available").default(false)

    override val primaryKey: PrimaryKey = PrimaryKey(menuId)
}