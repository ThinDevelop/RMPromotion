package com.rm.promotion.model

data class TemplateModel(
    val id: String = "",
    val type: String = "",//0 = QR, 1 = Barcode, 2 = Text Code
    val type_detail: String = "",//not empty is static
    val line_type: String = "",
    val detail: MutableList<TemplateDetailModel> = mutableListOf()
)