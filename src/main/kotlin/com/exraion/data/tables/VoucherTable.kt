package com.exraion.data.tables

import org.jetbrains.exposed.sql.Table

object VoucherTable: Table() {

    override val tableName: String = "voucher"

    val voucherId = varchar("voucher_id", 128)
    val voucherSecretRedeemKey = varchar("voucher_secret_redeem_key", 128).nullable()
    val xpCost = integer("xp_cost").default(0)
    val validUntil = varchar("valid_until", 64).default("")
    val category = varchar("category", 128).default("")
    val discount = integer("discount").default(0)
    val minimumSpend = integer("minimum_spend").default(0)
    val maximumDiscount = integer("maximum_discount").default(0)

    override val primaryKey: PrimaryKey = PrimaryKey(voucherId)
}