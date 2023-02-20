package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object ReviewTable: Table() {

    override val tableName: String = "review"

    val reviewId = varchar("review_id", 128)
    val uid = reference("uid", UserTable.uid)
    val menuId = reference("menuId", MenuTable.menuId)
    val rating = double("rating")

    override val primaryKey: PrimaryKey = PrimaryKey(reviewId)
}