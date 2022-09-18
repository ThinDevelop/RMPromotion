package com.rm.promotion.model

data class SlipModel(
    val login_time: String,
    val promotion_id: String,
    val product_id: String,
    val price: String,
    val number: String,
    val created_at: String,
    val detail: MutableList<SlipDetail>
)

data class SlipDetail(
    val promotion_id: String,
    val template_id: String,
    val number: String
)