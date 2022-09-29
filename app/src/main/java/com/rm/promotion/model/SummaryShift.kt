package com.rm.promotion.model

data class SummaryShift(
    var summary_date: String,
    var shift: String?,
    var promotion: MutableList<PromotionSummary>
)

data class PromotionSummary(
    val promotion_name: String,
    val promotion_code: String,
    val summary_slips: MutableList<SummarySlips>
)
