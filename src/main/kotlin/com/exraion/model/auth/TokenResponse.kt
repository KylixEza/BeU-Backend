package com.exraion.model.auth

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @field:SerializedName("token")
    val token: String
)
