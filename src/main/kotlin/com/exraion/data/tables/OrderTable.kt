package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object OrderTable: Table() {
	
	override val tableName: String = "order"
	
	val uid = reference("uid", UserTable.uid)
	val menuId = reference("menu_id", MenuTable.menuId)
	val orderId = varchar("order_id", 128)
	val timeStamp = varchar("time_stamp", 24).default("")
	val status = varchar("status", 24)
	val starsGiven = integer("stars").default(0)
	val totalPrice = integer("total_price").default(0)
	
	override val primaryKey: PrimaryKey = PrimaryKey(orderId)
}