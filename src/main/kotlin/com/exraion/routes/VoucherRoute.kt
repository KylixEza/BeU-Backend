package com.exraion.routes

import com.exraion.data.repositories.voucher.VoucherRepository
import com.exraion.middleware.Middleware
import com.exraion.model.voucher.VoucherBody
import com.exraion.routes.RouteResponseHelper.buildSuccessJson
import com.exraion.routes.RouteResponseHelper.buildSuccessListJson
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

class VoucherRoute(
    private val repository: VoucherRepository,
    private val middleware: Middleware,
) {

    private fun Route.postVoucher() {
        post("/voucher") {
            val body = call.receive<VoucherBody>()
            repository.insertVoucher(body)
            call.buildSuccessJson { "Voucher successfully inserted" }
        }
    }

    private fun Route.getAvailableVoucher() {
        authenticate {
            get("/voucher/available") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessJson { repository.getAvailableVoucher(uid) }
            }
        }
    }

    private fun Route.redeemVoucher() {
        authenticate {
            put("/voucher/{voucherId}/redeem") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val voucherId = call.parameters["voucherId"] ?: ""
                repository.redeemVoucher(uid, voucherId)
                call.buildSuccessJson { "Voucher successfully redeemed" }
            }
        }
    }

    private fun Route.getVoucherUser() {
        authenticate {
            get("/voucher/user") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                call.buildSuccessListJson { repository.getVoucherUser(uid) }
            }
        }
    }

    private fun Route.getDetailVoucher() {
        authenticate {
            get("/voucher/{voucherId}") {
                middleware.apply { call.validateToken() }
                val voucherId = call.parameters["voucherId"] ?: ""
                call.buildSuccessJson { repository.getDetailVoucher(voucherId) }
            }
        }
    }

    private fun Route.updateUsedVoucher() {
        authenticate {
            put("/voucher/{voucherId}/use") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val voucherId = call.parameters["voucherId"] ?: ""
                repository.updateUsedVoucher(uid, voucherId)
                call.buildSuccessJson { "Voucher successfully updated" }
            }
        }
    }

    private fun Route.searchVoucherUsingSecretKey() {
        authenticate {
            put("/voucher/secret/{secretKey}/redeem") {
                middleware.apply { call.validateToken() }
                val uid = middleware.getClaim(call, "uid") ?: ""
                val voucherSecretRedeemKey = call.parameters["secretKey"] ?: ""
                val response = repository.searchVoucherUsingSecretKey(uid, voucherSecretRedeemKey)
                call.buildSuccessJson { response }
            }
        }
    }

    fun Route.initRoute() {
        postVoucher()
        getAvailableVoucher()
        redeemVoucher()
        getVoucherUser()
        getDetailVoucher()
        updateUsedVoucher()
        searchVoucherUsingSecretKey()
    }

}