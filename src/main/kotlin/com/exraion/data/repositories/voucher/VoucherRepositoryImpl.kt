package com.exraion.data.repositories.voucher

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.exraion.data.database.DatabaseFactory
import com.exraion.data.tables.UserTable
import com.exraion.data.tables.VoucherTable
import com.exraion.data.tables.VoucherUserTable
import com.exraion.model.voucher.VoucherAvailableResponse
import com.exraion.model.voucher.VoucherBody
import com.exraion.model.voucher.VoucherListResponse
import com.exraion.model.voucher.VoucherSecretResponse
import com.exraion.util.VoucherCategory
import com.exraion.util.toVoucherDetailResponse
import com.exraion.util.toVoucherListResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus

class VoucherRepositoryImpl(
    private val dbFactory: DatabaseFactory
): VoucherRepository {
    override suspend fun insertVoucher(body: VoucherBody) {
        dbFactory.dbQuery {
            VoucherTable.insert {
                it[voucherId] = "VOUCHER${NanoIdUtils.randomNanoId()}"
                it[voucherSecretRedeemKey] = body.voucherSecretKey
                it[xpCost] = body.xpCost
                it[validUntil] = body.validUntil
                it[category] = body.category
                it[discount] = body.discount
                it[minimumSpend] = body.minimumSpend
                it[maximumDiscount] = body.maximumDiscount
            }
        }
    }

    override suspend fun getAvailableVoucher(uid: String): VoucherAvailableResponse = dbFactory.dbQuery {
        val shippingVoucher = VoucherTable.join(VoucherUserTable, JoinType.LEFT) {
            VoucherTable.voucherId.eq(VoucherUserTable.voucherId).and(VoucherUserTable.uid.eq(uid))
        }.select {
            VoucherUserTable.uid.isNull()
                .and(VoucherTable.category.eq(VoucherCategory.SHIPPING.category))
                .and(VoucherTable.voucherSecretRedeemKey.isNull())
        }.map {
            it.toVoucherListResponse()
        }

        val productVoucher = VoucherTable.join(VoucherUserTable, JoinType.LEFT) {
            VoucherTable.voucherId.eq(VoucherUserTable.voucherId).and(VoucherUserTable.uid.eq(uid))
        }.select {
            VoucherUserTable.uid.isNull()
                .and(VoucherTable.category.eq(VoucherCategory.PRODUCT.category))
                .and(VoucherTable.voucherSecretRedeemKey.isNull())
        }.map {
            it.toVoucherListResponse()
        }

        VoucherAvailableResponse(
            shippingVoucher, productVoucher
        )
    }

    override suspend fun redeemVoucher(uid: String, voucherId: String) {
        dbFactory.dbQuery {
            VoucherUserTable.insert {
                it[this.uid] = uid
                it[this.voucherId] = voucherId
                it[this.isUsed] = false
            }
            val xp = VoucherTable
                .select { VoucherTable.voucherId.eq(voucherId) }.firstNotNullOf { it[VoucherTable.xpCost] }

            UserTable.update(where = { UserTable.uid.eq(uid)}) {
                it[this.xp] = this.xp.minus(xp)
            }
        }
    }

    override suspend fun getVoucherUser(uid: String): List<VoucherListResponse> = dbFactory.dbQuery {
        VoucherUserTable.join(VoucherTable, JoinType.INNER) {
            VoucherUserTable.voucherId.eq(VoucherTable.voucherId)
        }.select {
            (VoucherUserTable.uid.eq(uid)) and (VoucherUserTable.isUsed.eq(false))
        }.mapNotNull {
            it.toVoucherListResponse()
        }
    }

    override suspend fun getDetailVoucher(voucherId: String) = dbFactory.dbQuery {
        VoucherTable.select {
            VoucherTable.voucherId.eq(voucherId)
        }.mapNotNull {
            it.toVoucherDetailResponse()
        }
    }.first()

    override suspend fun updateUsedVoucher(uid: String, voucherId: String) {
        dbFactory.dbQuery {
            VoucherUserTable.update(
                where = { (VoucherUserTable.uid.eq(uid)) and (VoucherUserTable.voucherId.eq(voucherId)) }
            ) {
                it[this.isUsed] = true
            }
        }
    }

    override suspend fun searchVoucherUsingSecretKey(uid: String, voucherSecretRedeemKey: String): VoucherSecretResponse = dbFactory.dbQuery {
        val isExist = VoucherTable.select {
            VoucherTable.voucherSecretRedeemKey.eq(voucherSecretRedeemKey)
        }.count() > 0

        val voucherId = VoucherTable.select {
            VoucherTable.voucherSecretRedeemKey.eq(voucherSecretRedeemKey)
        }.firstNotNullOf { it[VoucherTable.voucherId] }

        val isAlreadyClaimed = VoucherUserTable.select {
            (VoucherUserTable.uid.eq(uid)) and (VoucherUserTable.voucherId.eq(voucherId))
        }.count() > 0

        val response = if (isExist && isAlreadyClaimed.not()) {
            VoucherUserTable.insert {
                it[this.uid] = uid
                it[this.voucherId] = voucherId
                it[this.isUsed] = false
            }
            VoucherSecretResponse(
                true,
                "Voucher successfully claimed"
            )
        } else {
            VoucherSecretResponse(
                false,
                "Voucher already claimed or not exist"
            )
        }

        return@dbQuery response
    }
}