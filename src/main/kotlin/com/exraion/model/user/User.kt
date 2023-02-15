package com.exraion.model.user

data class User(
    val uid: String,
    val email: String,
    val name: String,
    val password: String,
    val salt: String,
)
