package com.rm.promotion.model

data class UserModel(
    val id: String,
    val username: String,
    val password: String,
    val password_mobile_pos: String,
    val name: String,
    val phone_number: String,
    val role_name: String,
)
