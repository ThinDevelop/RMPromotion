package com.rm.promotion.model

data class TemplateModel(
    val id: String = "",
    val type: String = "",
    val type_detail: String = "",
    val line_type: String = "",
    val detail: MutableList<TemplateDetailModel> = mutableListOf()
)