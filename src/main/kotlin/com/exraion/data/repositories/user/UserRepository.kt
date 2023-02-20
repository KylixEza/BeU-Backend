package com.exraion.data.repositories.user

import com.exraion.model.auth.RegisterBody
import com.exraion.model.review.ReviewBody
import com.exraion.model.user.User
import com.exraion.model.user.UserBody
import com.exraion.model.user.UserResponse
import com.exraion.security.hashing.SaltedHash

interface UserRepository {

    suspend fun insertUser(body: RegisterBody, saltedHash: SaltedHash): User
    suspend fun insertReview(uid: String, body: ReviewBody)
    suspend fun getUserByEmail(email: String): User? //clear
    suspend fun getDetailUser(uid: String): UserResponse //clear
    suspend fun updateUser(uid: String, body: UserBody) //clear
    suspend fun updateUserAvatar(uid: String, avatar: String) //clear
    suspend fun isEmailExist(email: String): Boolean //clear
    suspend fun insertFavorite(uid: String, menuId: String)
    suspend fun deleteFavorite(uid: String, menuId: String)

}