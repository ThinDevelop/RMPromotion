package com.rm.promotion

import androidx.annotation.DrawableRes

enum class ProductType(val code: String, @DrawableRes val res: Int) {
    B20("501212", R.drawable.product_b20),
    B7("500031", R.drawable.product_b7),
    BENZIN("500015", R.drawable.product_benzin),
    E20("500020", R.drawable.product_e20),
    E85("500023", R.drawable.product_e85),
    GASOHOL91("500028", R.drawable.product_gasohol91),
    GASOHOL95("500018", R.drawable.product_gasohol95),
    DIESEL("502530", R.drawable.product_diesel),
    POWER_DIESEL("501304", R.drawable.product_power_diesel),
    POWER_GASOHOL95("503380", R.drawable.product_power_gasohol95),
    UNSUPPORTED_FORMAT("UNSUPPORTED", R.drawable.product_power_gasohol95)
}
class ProductTypeManager {

    companion object {
        fun getProductType(code: String): ProductType {
            return when (code) {
                "501212" -> ProductType.B20
                "500031" -> ProductType.B7
                "500015" -> ProductType.BENZIN
                "500020" -> ProductType.E20
                "500023" -> ProductType.E85
                "500028" -> ProductType.GASOHOL91
                "500018" -> ProductType.GASOHOL95
                "502530" -> ProductType.DIESEL
                "501304" -> ProductType.POWER_DIESEL
                "503380" -> ProductType.POWER_GASOHOL95
                else -> ProductType.UNSUPPORTED_FORMAT
            }
        }
    }
}