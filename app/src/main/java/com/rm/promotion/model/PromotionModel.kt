package com.rm.promotion.model

data class PromotionModel(
    val id: String,
    val code: String,
    val name: String,
    val start_date: String,
    val end_date: String,
    val condition_type: String,
    val start_price: String = "1",
    val end_price: String,
    val use_num: String = "1",
    val use_max: String = "5",
    val priority: String,
    val child_id: String,
    val conditions: MutableList<ConditionModel>?,
    val templates: MutableList<TemplateModel>
)