package com.rm.promotion.model

data class CalculateResponseModel(
    val promotionId: String,
    val promotionTotal: Int,
    val templateModel: TemplateModel,
    val promotionName: String
)
