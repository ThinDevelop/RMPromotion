package com.rm.promotion.model

data class TransactionModel(
    val station_id: String,
    val mobile_pos_id: String,
    val business_date: String,
    val shift: String,
    val user_id: String,
    val slip: MutableList<SlipModel>,
    val login: MutableList<Login>
)

data class Login(
    val login_time: String,
    val business_date: String,
    val shift: String = "1",
    val user_id: String
)

