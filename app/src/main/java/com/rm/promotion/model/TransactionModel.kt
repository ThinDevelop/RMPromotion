package com.rm.promotion.model

data class TransactionModel(
    val station_id: String,
    val mobile_pos_id: String,
    val business_date: String,
    val shift: String,
    val user_id: String,
    val slip: MutableList<SlipModel>,
    val login: MutableList<Login>,
    val shift_data: MutableList<Shift>
)

data class Login(
    val login_time: String,
    val business_date: String,
    val shift: String = "1",
    val user_id: String
)

data class Shift(
    val business_date: String,
    val shift: String,
    val start_date: String,
    var end_date: String = "",
    var close_date: Boolean = false
)

