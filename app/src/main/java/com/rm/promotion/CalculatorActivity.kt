package com.rm.promotion

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
        val productCode = intent.getStringExtra("product_code")?:""
        productId = intent.getStringExtra("product_id")?:""
        productName = intent.getStringExtra("product_name")?:""
        productType = ProductTypeManager.getProductType(productCode)
        binding.imageView.setImageResource(productType.res)
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.include.txtBusinessDate.text = getString(R.string.header_business_date, PreferenceUtils.getBusinessDate())
        binding.include.txtSerialNo.text = getString(R.string.header_serial_no, android.os.Build.SERIAL)
        binding.include.txtShiftNo.text = getString(R.string.header_shift_no, PreferenceUtils.preferenceKeyCurrentShift)

        binding.btnCal.setOnClickListener {
            val strPrice = binding.price.text.toString().trim()
            if (strPrice.isNotEmpty()) {
                DialogUtils.showActionDialog(this@CalculatorActivity, getString(R.string.dialog_msg_print_price, strPrice.toDouble().toCurrency()), object : DialogUtils.OnClickButtonListener {
                    override fun onClick() {}

                    override fun getButtonKey(): String {
                        return getString(R.string.dialog_msg_cancel)
                    }
                }, object : DialogUtils.OnClickButtonListener {
                    override fun onClick() {
                        val calculatePrice = calculatePrice(strPrice)
                        val promotionId = calculatePrice.promotionId
                        val promotionCount = calculatePrice.promotionTotal
                        val templateModel = calculatePrice.templateModel
                        val createdAt = Date()
                        if (promotionCount > 0) {
                            val promotionTotal = if (promotionCount <= 9) "0$promotionCount" else promotionCount.toString()
                            val qrcode = QRUtils.getQRCode(promotionId, promotionTotal)
                            val linkQR = qrcode.first
                            val qrText = qrcode.second
                            print(templateModel, createdAt, productName, strPrice.toDouble().toCurrency(), linkQR, qrText, promotionCount.toString())

                            val slipDetail = SlipDetail(
                                promotion_id = promotionId,
                                template_id = templateModel.id,
                                number = promotionCount.toString()
                            )
                            val slipModel = SlipModel(
                                login_time = PreferenceUtils.loginTime,
                                promotion_id = promotionId,
                                product_id = productId,
                                price = strPrice,
                                number = promotionCount.toString(),
                                created_at = createdAt.time.toString(),
                                detail = mutableListOf(slipDetail)
                            )

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
                Toast.makeText(this@CalculatorActivity, R.string.login_please_field_number, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDialogNotMatch() {
        DialogUtils.showConfirmDialog(this@CalculatorActivity, getString(R.string.dialog_msg_price_not_match), object : DialogUtils.OnClickButtonListener {
            override fun onClick() {}

            override fun getButtonKey(): String {
                return getString(R.string.dialog_msg_close)
            }
        })
    }

    fun calculatePrice(strPrice: String) : CalculateResponseModel {
        val price = strPrice.toFloat()
        var count = 0
        var promotionLimit = 0
        var hasPromotion = false
        var promotionId = ""
        var templateModel = TemplateModel()
        var promotionName = ""

        PreferenceUtils.promotion.forEach { promotion ->

            val nowDate = Calendar.getInstance().time
            val startPrice = promotion.start_price.toFloat()
            val startDate = promotion.start_date.toDate(DateFormatConstant.yyyy_MM_dd_HH_mm_ss_SSS)
            val endDate = promotion.end_date.toDate(DateFormatConstant.yyyy_MM_dd_HH_mm_ss_SSS)
            try {
                promotionLimit = promotion.use_max.toInt()
            } catch (e : Exception) {
                promotionLimit = 10
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
                                    "1"-> {
                                        val cross = condition.special_num.toInt()
                                        if (count > 0 && cross > 0) {
                                            count *=cross
                                        }
                                    }
                                    "2"-> {
                                        val plus = condition.special_num.toInt()
                                        if (count > 0) {
                                            count += plus
                                        }
                                    }
                                    else->{}
                                }
                            }
                        }
                        if (count > 0) {
                            when (promotion.id.length) {
                                1 -> promotionId = "00"+promotion.id
                                2 -> promotionId = "0"+promotion.id
                                3 -> promotionId = promotion.id
                            }
                            promotionName = promotion.name
                            if(promotion.templates.size > 0) {
                                templateModel = promotion.templates.first()
                            }

                            hasPromotion = true
                            Log.d("calculator", "before break")
                            break
                        }
                    }
                }
            }
            if (hasPromotion) {
                Log.d("calculator", "hasPromotion : "+ hasPromotion)
                return@forEach
            }
        }
        return CalculateResponseModel(
            promotionId = promotionId,
            promotionTotal = if (count>promotionLimit) promotionLimit else count,
            templateModel = templateModel,
            promotionName = promotionName)
    }

    fun print(templateModel: TemplateModel,createdAt: Date, productName: String, price: String, qr: String, qrText: String, promotionTotal: String) {
        Toast.makeText(this@CalculatorActivity, "printing", Toast.LENGTH_LONG).show()
        val simpleDate = SimpleDateFormat(dd_MM_yyyy_HH_mm_ss)
        val currentDate = simpleDate.format(createdAt)
        val productName = arrayOf(productName, price)
        val total = arrayOf("Total", price)
        val cash = arrayOf("??????????????????/???????????????", price)
        val width = intArrayOf(1, 1)
        val align = intArrayOf(0, 2)
        val print_size = 6
        val error_level = 3
        SunmiPrintHelper.getInstance().setAlign(1)
        SunmiPrintHelper.getInstance().printTitle("\n\n" +PreferenceUtils.stationName)
        SunmiPrintHelper.getInstance().printSubtitle(PreferenceUtils.cashierName)
        SunmiPrintHelper.getInstance().printSubtitle("???????????????????????????#" + currentDate)
        SunmiPrintHelper.getInstance().printSplit2()

        SunmiPrintHelper.getInstance().printTable(productName, width, align)
        SunmiPrintHelper.getInstance().printTable(total, width, align)
        SunmiPrintHelper.getInstance().printTable(cash, width, align)
        SunmiPrintHelper.getInstance().printSplit2()
        SunmiPrintHelper.getInstance().printSubtitle("1365 Contact Center")
        SunmiPrintHelper.getInstance().printSubtitle("==VAT INCLUDED==")
        SunmiPrintHelper.getInstance().printSubtitle("THANK YOU AND WELCOME")
        SunmiPrintHelper.getInstance().printSplit1()

//        when (templateModel.line_type) {
//            "0"->{SunmiPrintHelper.getInstance().printSplitCut(this@CalculatorActivity)}
//            "1"->{SunmiPrintHelper.getInstance().printSplit1()}
//            else->{}
//        }

        val details = templateModel.detail
        if (details.size > 0) {
            for (detail in details) {
                var fontSize = 20f
                if ("0".equals(detail.type)) {
                    try {
                        fontSize = detail.text_font.toFloat()
                    } catch (e: Exception) {

                    }
                SunmiPrintHelper.getInstance().printWithSize(detail.text_detail, fontSize*1.8f)
                }
            }
            SunmiPrintHelper.getInstance().printTitle("??????????????? $promotionTotal ??????????????????")
            SunmiPrintHelper.getInstance().printQr(qr, print_size, error_level)
            SunmiPrintHelper.getInstance().printSubtitle(qrText)
            SunmiPrintHelper.getInstance().setAlign(0)

            for (detail in details) {
                var fontSize = 20f
                if ("1".equals(detail.type)) {
                    try {
                        fontSize = detail.text_font.toFloat()
                    } catch (e: Exception) {

                    }

                    SunmiPrintHelper.getInstance().printWithSize(detail.text_detail, fontSize*1.5f)
                }
            }
//            SunmiPrintHelper.getInstance().printText("???????????????????????????????????????????????????\n" +
//                    "1. ??????????????????????????????????????? ????????????????????????????????? ??????????????????????????????????????????????????????\n" +
//                    "    ????????? ????????????????????? 1 ???.???. 65 - 31 ???.???. 65\n" +
//                    "2. ????????????????????????-????????????????????? ????????????????????? ????????????????????????????????????????????????\n" +
//                    "3. ?????? ?????????????????? ??????????????????????????????????????????????????????????????????????????????????????????\n" +
//                    "    ?????????????????? ?????????????????? ?????????????????????\n" +
//                    "4. ????????????????????? 1 ???????????????????????????????????????????????????????????????????????????????????????\n" +
//                    "    ??????????????????????????????\n" +
//                    "5. ????????????????????????????????????????????????????????????????????????. 1365 Contact Call\n" +
//                    "    Center\n" +
//                    "6. ?????????????????????????????????????????????????????????????????????????????????????????? ????????????????????????\n" +
//                    "    ???????????????????????????????????????????????????????????? https://posoilluckytest.\n" +
//                    "    pttor.com/Register ???????????????????????????????????????????????????????????????\n" +
//                    "    ?????????????????????????????????")


        } else {
            SunmiPrintHelper.getInstance().printTitle("??????????????? $promotionTotal ??????????????????")
            SunmiPrintHelper.getInstance().printQr(qr, print_size, error_level)
            SunmiPrintHelper.getInstance().printSubtitle(qrText)
        }
        SunmiPrintHelper.getInstance().feedPaper()
        SunmiPrintHelper.getInstance().feedPaper()
    }
}