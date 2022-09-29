package com.rm.promotion.model

data class GetReportResponseModel(
    val summary_date: String,
    val shift: String?,
    val promotion: MutableList<PromotionReport>
)

data class PromotionReport(
    val promotion_name: String,
    val promotion_code: String,
    val summary_slips: MutableList<SummarySlips>
)

data class SummarySlips(
    var numbers: String,
    var slips: String
)