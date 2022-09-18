package com.rm.promotion.model

data class CloseShiftModel(
val business_date: String,
val user_id: String,
val shift: String,
val close_type: String,
val station_id: String,
val mobile_pos_id: String,
val slip: MutableList<CloseSlips>,
val login: MutableList<CloseSlipLogin>
)

data class CloseSlips(
    val login_time: String,
    val promotion_id: String,
    val product_id: String,
    val price: String,
    val number: String,
    val detail: MutableList<CloseSlipDetail>
)

data class CloseSlipDetail(
    val promotion_id: String,
    val template_id: String,
    val number: String
)

data class CloseSlipLogin(
    val login_time: String,
    val business_date: String,
    val shift: String,
    val user_id: String
)