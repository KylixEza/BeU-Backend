package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object OrderIngredientTable: Table() {

    override val tableName: String = "order_ingredient"

    val orderId = reference("order_id", OrderTable.orderId)
    val ingredient = varchar("ingredient", 128)
}