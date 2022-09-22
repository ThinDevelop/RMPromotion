package com.rm.promotion

import androidx.annotation.DrawableRes

enum class ProductType(val code: String, @DrawableRes val res: Int) {
    B20("016", R.drawable.product_b20),
    B7("004", R.drawable.product_b7),
    BENZIN("001", R.drawable.product_benzin),
    E20("008", R.drawable.product_e20),
    E85("009", R.drawable.product_e85),
    GASOHOL91("006", R.drawable.product_gasohol91),
    GASOHOL95("005", R.drawable.product_gasohol95),
    DIESEL("017", R.drawable.product_diesel),
    POWER_DIESEL("015", R.drawable.product_power_diesel),
    POWER_GASOHOL95("018", R.drawable.product_power_gasohol95),
    UNSUPPORTED_FORMAT("UNSUPPORTED", R.drawable.product_power_gasohol95)
}
class ProductTypeManager {

    companion object {
        fun getProductType(code: String): ProductType {
            return when (code) {
                "016" -> ProductType.B20
                "004" -> ProductType.B7
                "001" -> ProductType.BENZIN
                "008" -> ProductType.E20
                "009" -> ProductType.E85
                "006" -> ProductType.GASOHOL91
                "005" -> ProductType.GASOHOL95
                "017" -> ProductType.DIESEL
                "015" -> ProductType.POWER_DIESEL
                "018" -> ProductType.POWER_GASOHOL95
                else -> ProductType.UNSUPPORTED_FORMAT
            }
        }
    }
}