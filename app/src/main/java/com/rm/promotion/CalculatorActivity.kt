package com.rm.promotion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rm.promotion.databinding.ActivityCalcuratorBinding
import com.rm.promotion.model.*
import com.rm.promotion.model.DateFormatConstant.dd_MM_yyyy_HH_mm_ss
import com.rm.promotion.util.*
import java.text.SimpleDateFormat
import java.util.*

class CalculatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalcuratorBinding
    var productType = ProductType.UNSUPPORTED_FORMAT
    var productId = ""
    var productName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalcuratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val productCode = intent.getStringExtra("product_code") ?: ""
        productId = intent.getStringExtra("product_id") ?: ""
        productName = intent.getStringExtra("product_name") ?: ""
        productType = ProductTypeManager.getProductType(productCode)
        binding.imageView.setImageResource(productType.res)
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.include.txtBusinessDate.text =
            getString(R.string.header_business_date, PreferenceUtils.getBusinessDate())
        binding.include.txtSerialNo.text =
            getString(R.string.header_serial_no, android.os.Build.SERIAL)
        binding.include.txtShiftNo.text =
            getString(R.string.header_shift_no, PreferenceUtils.preferenceKeyCurrentShift)

        binding.btnCal.setOnClickListener {
            val strPrice = binding.price.text.toString().trim()
            if (strPrice.isNotEmpty()) {
                DialogUtils.showActionDialog(
                    this@CalculatorActivity,
                    getString(R.string.dialog_msg_print_price, strPrice.toDouble().toCurrency()),
                    object : DialogUtils.OnClickButtonListener {
                        override fun onClick() {}

                        override fun getButtonKey(): String {
                            return getString(R.string.dialog_msg_cancel)
                        }
                    },
                    object : DialogUtils.OnClickButtonListener {
                        override fun onClick() {
                            val calculatePrice = calculatePrice(strPrice)
                            val promotionId = calculatePrice.promotionId
                            val promotionCount = calculatePrice.promotionTotal
                            val templateModels = calculatePrice.templateModel
                            val childPromotion = calculatePrice.childPromotion
                            val slipDetails = mutableListOf<SlipDetail>()
                            val createdAt = Date()
                            if (promotionCount > 0) {

                                val promotionTotal =
                                    if (promotionCount <= 9) "0$promotionCount" else promotionCount.toString()
                                val qrcode = QRUtils.getQRCode(promotionId, promotionTotal)
                                val linkQR = qrcode.first
                                val qrText = qrcode.second
                                RMPrintUtil.printPromotion(
                                    this@CalculatorActivity,
                                    templateModels,
                                    createdAt,
                                    productName,
                                    strPrice.toDouble().toCurrency(),
                                    linkQR,
                                    qrText,
                                    promotionCount.toString()
                                )

                                val calChildPromotion =
                                    calculatePriceOfChildPromotion(strPrice, childPromotion)
                                val calPromotionId = calChildPromotion.promotionId
                                val calPromotionCount = calChildPromotion.promotionTotal
                                val calTemplateModel = calChildPromotion.templateModel
                                if (calPromotionCount > 0) {
                                    val childPromotionTotal =
                                        if (calPromotionCount <= 9) "0$calPromotionCount" else calPromotionCount.toString()

                                    val childQrcode =
                                        QRUtils.getQRCode(calPromotionId, childPromotionTotal)
                                    val childLinkQR = childQrcode.first
                                    val childQrText = childQrcode.second

                                    RMPrintUtil.printChildPromotion(
                                        this@CalculatorActivity,
                                        calTemplateModel,
                                        createdAt,
                                        productName,
                                        strPrice.toDouble().toCurrency(),
                                        childLinkQR,
                                        childQrText,
                                        calPromotionCount.toString()
                                    )
                                }

                                templateModels.forEach {
                                    slipDetails.add(
                                        SlipDetail(
                                            promotion_id = promotionId,
                                            template_id = it.id,
                                            number = promotionCount.toString()
                                        )
                                    )
                                }

                                val slipModel = SlipModel(
                                    login_time = PreferenceUtils.loginTime,
                                    promotion_id = promotionId,
                                    product_id = productId,
                                    price = strPrice,
                                    number = promotionCount.toString(),
                                    created_at = createdAt.time.toString(),
                                    detail = slipDetails
                                )
                                val promotionSummary = PromotionSummary(
                                    promotion_name = calculatePrice.promotionName,
                                    promotion_code = promotionId,
                                    summary_slips = arrayListOf(
                                        SummarySlips(
                                            numbers = promotionCount.toString(),
                                            slips = "1"
                                        )
                                    )
                                )
                                FileUtils.addSlip(this@CalculatorActivity, promotionSummary)
                                FileUtils.saveJsonFile(this@CalculatorActivity, slipModel)
                                this@CalculatorActivity.finish()
                            } else {
                                showDialogNotMatch()
                            }
                        }

                        override fun getButtonKey(): String {
                            return getString(R.string.dialog_msg_ok)
                        }
                    })
            } else {
                Toast.makeText(
                    this@CalculatorActivity,
                    R.string.login_please_field_number,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showDialogNotMatch() {
        DialogUtils.showConfirmDialog(
            this@CalculatorActivity,
            getString(R.string.dialog_msg_price_not_match),
            object : DialogUtils.OnClickButtonListener {
                override fun onClick() {}

                override fun getButtonKey(): String {
                    return getString(R.string.dialog_msg_close)
                }
            })
    }

    fun calculatePrice(strPrice: String): CalculateResponseModel {
        val price = strPrice.toFloat()
        var count = 0
        var promotionLimit = 0
        var hasPromotion = false
        var promotionId = ""
        var templateModel = mutableListOf<TemplateModel>()
        var promotionName = ""
        var childPromotion: PromotionModel? = null
        val promotions = getPromotionWithPriority(strPrice)

        promotions.forEach { promotion ->
            val nowDate = Calendar.getInstance().time
            val startPrice = promotion.start_price.toFloat()
            val startDate = promotion.start_date.toDate(DateFormatConstant.yyyy_MM_dd_HH_mm_ss_SSS)
            val endDate = promotion.end_date.toDate(DateFormatConstant.yyyy_MM_dd_HH_mm_ss_SSS)
            try {
                promotionLimit = promotion.use_max.toInt()
            } catch (e: Exception) {
                promotionLimit = 99
            }

            if (nowDate.after(startDate) && nowDate.before(endDate) && price >= startPrice) {
                promotion.conditions?.let { conditions ->
                    for (condition in conditions) {
                        Log.d("calculator", "start for")
                        if (productId == condition.product_id) {
                            childPromotion = promotion.child_promotion
                            val conditionType = promotion.condition_type
                            if ("0".equals(conditionType)) {
                                count = price.div(startPrice).toInt()
                            } else if ("1".equals(conditionType)) {
                                val endPrice = promotion.end_price.toFloat()
                                if (price in startPrice..endPrice) {
                                    count = 1
                                } else {
                                    count = 0
                                }
                            }
                            if (!condition.special_type.isEmpty()) {
                                when (condition.special_type) {
                                    "1" -> {
                                        val cross = condition.special_num.toInt()
                                        if (count > 0 && cross > 0) {
                                            count *= cross
                                        }
                                    }
                                    "2" -> {
                                        val plus = condition.special_num.toInt()
                                        if (count > 0) {
                                            count += plus
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                        if (count > 0) {
                            when (promotion.code.length) {
                                1 -> promotionId = "00" + promotion.code
                                2 -> promotionId = "0" + promotion.code
                                3 -> promotionId = promotion.code
                            }
                            promotionName = promotion.name
                            if (promotion.templates.size > 0) {
                                templateModel = promotion.templates
                            }

                            hasPromotion = true
                            Log.d("calculator", "before break")
                            break
                        }
                    }
                }
            }
            if (hasPromotion) {
                Log.d("calculator", "hasPromotion : " + hasPromotion)
                return@forEach
            }
        }
        return CalculateResponseModel(
            promotionId = promotionId,
            promotionTotal = if (count > promotionLimit) promotionLimit else count,
            templateModel = templateModel,
            promotionName = promotionName,
            childPromotion = childPromotion
        )
    }

    fun calculatePriceOfChildPromotion(
        strPrice: String,
        promotion: PromotionModel?
    ): CalculateResponseModel {
        val price = strPrice.toFloat()
        var count = 0
        var promotionLimit = 0
        var promotionId = ""
        var templateModel = mutableListOf<TemplateModel>()
        var promotionName = ""

        promotion?.let {
            val nowDate = Calendar.getInstance().time
            val startPrice = promotion.start_price.toFloat()
            val startDate = promotion.start_date.toDate(DateFormatConstant.yyyy_MM_dd_HH_mm_ss_SSS)
            val endDate = promotion.end_date.toDate(DateFormatConstant.yyyy_MM_dd_HH_mm_ss_SSS)
            try {
                promotionLimit = promotion.use_max.toInt()
            } catch (e: Exception) {
                promotionLimit = 99
            }

            if (nowDate.after(startDate) && nowDate.before(endDate) && price >= startPrice) {
                promotion.conditions?.let { conditions ->
                    for (condition in conditions) {
                        Log.d("calculator", "start for")
                        if (productId == condition.product_id) {
                            val conditionType = promotion.condition_type
                            if ("0".equals(conditionType)) {
                                count = price.div(startPrice).toInt()
                            } else if ("1".equals(conditionType)) {
                                val endPrice = promotion.end_price.toFloat()
                                if (price in startPrice..endPrice) {
                                    count = 1
                                } else {
                                    count = 0
                                }
                            }
                            if (!condition.special_type.isEmpty()) {
                                when (condition.special_type) {
                                    "1" -> {
                                        val cross = condition.special_num.toInt()
                                        if (count > 0 && cross > 0) {
                                            count *= cross
                                        }
                                    }
                                    "2" -> {
                                        val plus = condition.special_num.toInt()
                                        if (count > 0) {
                                            count += plus
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                        if (count > 0) {
                            when (promotion.code.length) {
                                1 -> promotionId = "00" + promotion.code
                                2 -> promotionId = "0" + promotion.code
                                3 -> promotionId = promotion.code
                            }
                            promotionName = promotion.name
                            if (promotion.templates.size > 0) {
                                templateModel = promotion.templates
                            }
                        }
                    }
                }
            }
        }
        return CalculateResponseModel(
            promotionId = promotionId,
            promotionTotal = if (count > promotionLimit) promotionLimit else count,
            templateModel = templateModel,
            promotionName = promotionName,
            childPromotion = null
        )
    }

    fun getPromotionWithPriority(strPrice: String): List<PromotionModel> {
        val price = strPrice.toFloat()
        val promotionList = mutableListOf<PromotionModel>()
        PreferenceUtils.promotion.forEach { promotion ->

            val nowDate = Calendar.getInstance().time
            val startPrice = promotion.start_price.toFloat()
            val startDate = promotion.start_date.toDate(DateFormatConstant.yyyy_MM_dd_HH_mm_ss_SSS)
            val endDate = promotion.end_date.toDate(DateFormatConstant.yyyy_MM_dd_HH_mm_ss_SSS)

            if (nowDate.after(startDate) && nowDate.before(endDate) && price >= startPrice) {
                promotionList.add(promotion)
            }
        }
        return promotionList.sortedBy { it.priority }
    }
}