package com.rm.promotion.model

data class RMDataModel(
    val station: StationModel,
    val users: MutableList<UserModel>,
    val products: MutableList<ProductModel>,
    val promotions: MutableList<PromotionModel>
)