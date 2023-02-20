package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object ToolTable: Table() {

    override val tableName: String = "tool"

    val menuId = reference("menu_id", MenuTable.menuId)
    val tool = varchar("tool", 128)
}