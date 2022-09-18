package com.rm.promotion.model

data class LoginResponseModel(
    val station: StationModel,
    val profile: Profile,
    val users: MutableList<UserModel>,
    val products: MutableList<ProductModel>,
    val promotions_sync: Boolean,
    val shift: ShiftModel
)

data class Profile(
    val id: String,
    val username: String,
    val name: String,
    val password_mobile_pos: String
)