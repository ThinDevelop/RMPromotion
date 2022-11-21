package com.rm.promotion.model

data class CalculateResponseModel(
    val promotionId: String,
    val promotionTotal: Int,
    val templateModel: MutableList<TemplateModel>,
    val promotionName: String,
    val childPromotion: PromotionModel?,
    val printType: Int
)
