package com.rm.promotion.model

data class SummaryDate(
    var summary_date: String,
    var shift: String?,
    var promotion: MutableList<PromotionSummary>
)
