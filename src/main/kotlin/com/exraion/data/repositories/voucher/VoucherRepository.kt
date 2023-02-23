package com.exraion.data.repositories.voucher

import com.exraion.model.voucher.VoucherBody
import com.exraion.model.voucher.VoucherAvailableResponse
import com.exraion.model.voucher.VoucherDetailResponse
import com.exraion.model.voucher.VoucherListResponse

interface VoucherRepository {
    suspend fun insertVoucher(body: VoucherBody)
    suspend fun getAvailableVoucher(uid: String): VoucherAvailableResponse //clear
    suspend fun redeemVoucher(uid: String, voucherId: String) //clear
    suspend fun getVoucherUser(uid: String): List<VoucherListResponse> //clear
    suspend fun getDetailVoucher(voucherId: String): VoucherDetailResponse //clear
    suspend fun updateUsedVoucher(uid: String, voucherId: String)
    suspend fun searchVoucherUsingSecretKey(uid: String, voucherSecretRedeemKey: String): Boolean
}