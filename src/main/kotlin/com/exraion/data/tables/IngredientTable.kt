package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object IngredientTable: Table() {

    override val tableName: String = "ingredient"

    val menuId = reference("menu_id", MenuTable.menuId)
    val ingredient = varchar("ingredient", 128)
}