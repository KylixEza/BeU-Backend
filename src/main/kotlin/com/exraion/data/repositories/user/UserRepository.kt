package com.exraion.data.repositories.user

import com.exraion.model.auth.RegisterBody
import com.exraion.model.history.HistoryResponse
import com.exraion.model.order.OrderBody
import com.exraion.model.user.User
import com.exraion.model.user.UserBody
import com.exraion.model.user.UserResponse
import com.exraion.security.hashing.SaltedHash
import io.ktor.http.content.*

interface UserRepository {

    suspend fun insertUser(body: RegisterBody, saltedHash: SaltedHash): User //clear
    suspend fun insertOrder(uid: String, body: OrderBody)
    suspend fun getUserByEmail(email: String): User? //clear
    suspend fun getDetailUser(uid: String): UserResponse //clear
    suspend fun updateUser(uid: String, body: UserBody) //clear
    suspend fun updateUserAvatar(uid: String, part: PartData.FileItem) //clear
    suspend fun isEmailExist(email: String): Boolean //clear
    suspend fun insertFavorite(uid: String, menuId: String) //clear
    suspend fun deleteFavorite(uid: String, menuId: String) //clear
    suspend fun getOrderHistory(uid: String): List<HistoryResponse>
    suspend fun cancelOrder(orderId: String)//clear
    suspend fun updateOrderStars(uid: String, orderId: String, stars: Double)

}