package com.rm.promotion.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.rm.promotion.R
import com.rm.promotion.model.*
import com.rm.promotion.toDate
import java.text.SimpleDateFormat
import java.util.*

class RMPrintUtil {

    companion object {

        fun printReportDay(context: Context, reportModel: SummaryDate) {
            if (reportModel.summary_date == null) {
                reportModel.summary_date = PreferenceUtils.preferenceKeyBusinessDate
            }
            val summaryDate = reportModel.summary_date
            val promotions = reportModel.promotion
            var shift = ""

            val dateFormated = summaryDate.toDate(DateFormatConstant.yyyy_M_dd)
            val simpleDate = SimpleDateFormat(DateFormatConstant.dd_M_yyyy, Locale.getDefault())
            val date = simpleDate.format(dateFormated)

            val width = intArrayOf(1, 1)
            val align = intArrayOf(0, 2)
            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printTitle("\n\n" + PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle("Station ID " + PreferenceUtils.stationId)
            SunmiPrintHelper.getInstance()
                .printSubtitle("Cashier Name " + PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance()
                .printSubtitle("Cashier ID " + PreferenceUtils.currentUserId)
            SunmiPrintHelper.getInstance().printSubtitle("ใบสรุปยอดวันที่#$date $shift")
            SunmiPrintHelper.getInstance().printSplit2()

            val header = arrayOf(
                context.getString(R.string.header_promotion),
                context.getString(R.string.header_summary)
            )
            SunmiPrintHelper.getInstance().printTable(header, width, align, false)
            SunmiPrintHelper.getInstance().setAlign(0)
            promotions?.let {
                for (promotion in promotions) {
                    val promotionId = promotion.promotion_code
                    val promotionName = promotion.promotion_name
                    SunmiPrintHelper.getInstance().printSubtitle("$promotionName รหัส $promotionId")
                    for (slip in promotion.summary_slips) {
                        val slipNumber = slip.slips
                        val promotionNumber = slip.numbers
                        val slipDetail =
                            arrayOf("จำนวน $promotionNumber สิทธิ์", "พิมพ์ $slipNumber ใบ")
                        SunmiPrintHelper.getInstance().printTable(slipDetail, width, align, false)
                    }
                }
            }

            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printSplit2()
            SunmiPrintHelper.getInstance().printSubtitle("1365 Contact Center")
            SunmiPrintHelper.getInstance().printSubtitle("==VAT INCLUDED==")
            SunmiPrintHelper.getInstance().printSubtitle("THANK YOU AND WELCOME\n\n")
            SunmiPrintHelper.getInstance().feedPaper()
        }


        fun printReportShift(context: Context, reportModel: SummaryShift) {
            if (reportModel.summary_date == null) {
                reportModel.summary_date = PreferenceUtils.preferenceKeyBusinessDate
            }
            if (reportModel.shift == null) {
                reportModel.shift = PreferenceUtils.preferenceKeyCurrentShift
            }
            val summaryDate = reportModel.summary_date
            val promotions = reportModel.promotion
            var shift = reportModel.shift

            if (shift != null && !shift.equals("null")) {
                shift = "กะ$shift"
            } else {
                shift = ""
            }

            val dateFormated = summaryDate.toDate(DateFormatConstant.yyyy_M_dd)
            val simpleDate = SimpleDateFormat(DateFormatConstant.dd_M_yyyy, Locale.getDefault())
            val date = simpleDate.format(dateFormated)

            val width = intArrayOf(1, 1)
            val align = intArrayOf(0, 2)
            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printTitle("\n\n" + PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle("Station ID " + PreferenceUtils.stationId)
            SunmiPrintHelper.getInstance()
                .printSubtitle("Cashier Name " + PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance()
                .printSubtitle("Cashier ID " + PreferenceUtils.currentUserId)
            SunmiPrintHelper.getInstance().printSubtitle("ใบสรุปยอดวันที่#$date $shift")
            SunmiPrintHelper.getInstance().printSplit2()

            val header = arrayOf(
                context.getString(R.string.header_promotion),
                context.getString(R.string.header_summary)
            )
            SunmiPrintHelper.getInstance().printTable(header, width, align, false)
            SunmiPrintHelper.getInstance().setAlign(0)
            promotions?.let {
                for (promotion in promotions) {
                    val promotionId = promotion.promotion_code
                    val promotionName = promotion.promotion_name
                    SunmiPrintHelper.getInstance().printSubtitle("$promotionName รหัส $promotionId")
                    for (slip in promotion.summary_slips) {
                        val slipNumber = slip.slips
                        val promotionNumber = slip.numbers
                        val slipDetail =
                            arrayOf("จำนวน $promotionNumber สิทธิ์", "พิมพ์ $slipNumber ใบ")
                        SunmiPrintHelper.getInstance().printTable(slipDetail, width, align, false)
                    }
                }
            }
            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printSplit2()
            SunmiPrintHelper.getInstance().printSubtitle("1365 Contact Center")
            SunmiPrintHelper.getInstance().printSubtitle("==VAT INCLUDED==")
            SunmiPrintHelper.getInstance().printSubtitle("THANK YOU AND WELCOME\n\n")
            SunmiPrintHelper.getInstance().feedPaper()
        }

        fun printReport(reportModel: GetReportResponseModel, context: Context) {
            val summaryDate = reportModel.summary_date
            val promotions = reportModel.promotion
            var shift = reportModel.shift

            if (shift != null && !shift.equals("null")) {
                shift = "กะ$shift"
            } else {
                shift = ""
            }

            val dateFormated = summaryDate.toDate(DateFormatConstant.yyyy_M_dd)
            val simpleDate = SimpleDateFormat(DateFormatConstant.dd_M_yyyy, Locale.getDefault())
            val date = simpleDate.format(dateFormated)

            val width = intArrayOf(1, 1)
            val align = intArrayOf(0, 2)
            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printTitle("\n\n" + PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle("Station ID " + PreferenceUtils.stationId)
            SunmiPrintHelper.getInstance()
                .printSubtitle("Cashier Name " + PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance()
                .printSubtitle("Cashier ID " + PreferenceUtils.currentUserId)
            SunmiPrintHelper.getInstance().printSubtitle("ใบสรุปยอดวันที่#$date $shift")
            SunmiPrintHelper.getInstance().printSplit2()

            val header = arrayOf(
                context.getString(R.string.header_promotion),
                context.getString(R.string.header_summary)
            )
            SunmiPrintHelper.getInstance().printTable(header, width, align, false)
            SunmiPrintHelper.getInstance().setAlign(0)
            for (promotion in promotions) {
                val promotionId = promotion.promotion_code
                val promotionName = promotion.promotion_name
                SunmiPrintHelper.getInstance().printSubtitle("$promotionName รหัส $promotionId")
                for (slip in promotion.summary_slips) {
                    val slipNumber = slip.slips
                    val promotionNumber = slip.numbers
                    val slipDetail =
                        arrayOf("จำนวน $promotionNumber สิทธิ์", "พิมพ์ $slipNumber ใบ")
                    SunmiPrintHelper.getInstance().printTable(slipDetail, width, align, false)
                }
            }

            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printSplit2()
            SunmiPrintHelper.getInstance().printSubtitle("1365 Contact Center")
            SunmiPrintHelper.getInstance().printSubtitle("==VAT INCLUDED==")
            SunmiPrintHelper.getInstance().printSubtitle("THANK YOU AND WELCOME\n\n")
            SunmiPrintHelper.getInstance().feedPaper()
        }


        fun printPromotion(
            context: Context,
            templateModels: MutableList<TemplateModel>,
            createdAt: Date,
            productName: String,
            price: String,
            qr: String,
            qrText: String,
            promotionTotal: String,
            hasChild: Boolean,
            printType: Int
        ) {
            val simpleDate = SimpleDateFormat(DateFormatConstant.dd_MM_yyyy_HH_mm_ss)
            val currentDate = simpleDate.format(createdAt)
            val productName = arrayOf(productName, price)
            val total = arrayOf("Total", price)
            val cash = arrayOf("เงินสด/อื่นๆ", price)
            val width = intArrayOf(1, 1)
            val align = intArrayOf(0, 2)
            val print_size = 6
            val error_level = 3
            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printTitle("\n\n" + PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle(PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance().printSubtitle("วันที่ขาย#" + currentDate)
            SunmiPrintHelper.getInstance().printSplit2()

            SunmiPrintHelper.getInstance().printTable(productName, width, align)
            SunmiPrintHelper.getInstance().printTable(total, width, align)
            SunmiPrintHelper.getInstance().printTable(cash, width, align)
            SunmiPrintHelper.getInstance().printSplit2()
            SunmiPrintHelper.getInstance().printSubtitle("1365 Contact Center")
            SunmiPrintHelper.getInstance().printSubtitle("==VAT INCLUDED==")
            SunmiPrintHelper.getInstance().printSubtitle("THANK YOU AND WELCOME")

            templateModels.forEach { templateModel ->
                if (printType == 1) {
                    for (i in 1..promotionTotal.toInt()) {
                        printFromTemplate(
                            context,
                            qr,
                            qrText,
                            "1",
                            templateModel
                        )
                    }
                } else {
                    printFromTemplate(
                        context,
                        qr,
                        qrText,
                        promotionTotal,
                        templateModel
                    )
                }
            }
            if (!hasChild) {
                SunmiPrintHelper.getInstance().feedPaper()
                SunmiPrintHelper.getInstance().feedPaper()
            }
        }

        private fun printFromTemplate(
            context: Context,
            qr: String,
            qrText: String,
            promotionTotal: String,
            templateModel: TemplateModel
        ) {
            val print_size = 6
            val error_level = 3
            when (templateModel.line_type) {
                "0" -> {
                    SunmiPrintHelper.getInstance().printSplitCut(context)
                }
                "1" -> {
                    SunmiPrintHelper.getInstance().printSplit1()
                }
                else -> {
                    SunmiPrintHelper.getInstance().printSplitSpace()
                }
            }
            SunmiPrintHelper.getInstance().setAlign(1)

            val details = templateModel.detail
            if (details.size > 0) {
                for (detail in details) {
                    var fontSize = 20f
                    if ("0".equals(detail.type)) {
                        fontSize = detail.text_font.toFloat()
                        SunmiPrintHelper.getInstance()
                            .printWithSize(detail.text_detail, fontSize * 1.4f)
                    }
                }
                SunmiPrintHelper.getInstance().printTitle("\nจำนวน $promotionTotal สิทธิ์")
                when (templateModel.type) {
                    "0" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance()
                                .printQr(templateModel.type_detail, print_size, error_level)
                        } else {
                            SunmiPrintHelper.getInstance().printQr(qr, print_size, error_level)
                        }
                        SunmiPrintHelper.getInstance().printSubtitleNoNewLine(qrText)
                    }
                    "1" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance()
                                .printBarcode(templateModel.type_detail)
                        } else {
//                            SunmiPrintHelper.getInstance()
//                                .printBarcode(templateModel.type_detail)
                        }
                    }
                    "2" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance().printTitle(templateModel.type_detail)
                        }
                    }
                }
                SunmiPrintHelper.getInstance().printText("\n")
                SunmiPrintHelper.getInstance().setAlign(0)

                for (detail in details) {
                    var fontSize = 20f
                    if ("1".equals(detail.type)) {
                        fontSize = detail.text_font.toFloat()
                        SunmiPrintHelper.getInstance()
                            .printWithSize(detail.text_detail, fontSize * 1.5f)
                    }
                }
            } else {
                SunmiPrintHelper.getInstance().printTitle("จำนวน $promotionTotal สิทธิ์")
                when (templateModel.type) {
                    "0" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance()
                                .printQr(templateModel.type_detail, print_size, error_level)
                        } else {
                            SunmiPrintHelper.getInstance().printQr(qr, print_size, error_level)
                        }
                        SunmiPrintHelper.getInstance().printSubtitle(qrText)
                    }
                    "1" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance()
                                .printBarcode(templateModel.type_detail)
                        } else {
//                            SunmiPrintHelper.getInstance().printBarcode("barcode dynamic")
                        }
                    }
                    "2" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance().printTitle(templateModel.type_detail)
                        }
                    }
                }
            }
        }

        fun printChildPromotion(
            context: Context,
            templateModels: MutableList<TemplateModel>,
            createdAt: Date,
            productName: String,
            price: String,
            qr: String,
            qrText: String,
            promotionTotal: String,
            printType: Int
        ) {
            val simpleDate = SimpleDateFormat(DateFormatConstant.dd_MM_yyyy_HH_mm_ss)
            val print_size = 6
            val error_level = 3
            SunmiPrintHelper.getInstance().setAlign(1)
            templateModels.forEach { templateModel ->
                if (printType == 1) {
                    for (i in 1..promotionTotal.toInt()) {
                        printChildPromotionFromTemplate(
                            context,
                            qr,
                            qrText,
                            "1",
                            templateModel
                        )
                    }
                } else {
                    printChildPromotionFromTemplate(
                        context,
                        qr,
                        qrText,
                        promotionTotal,
                        templateModel
                    )
                }
            }
            SunmiPrintHelper.getInstance().feedPaper()
            SunmiPrintHelper.getInstance().feedPaper()
        }

        private fun printChildPromotionFromTemplate(
            context: Context,
            qr: String,
            qrText: String,
            promotionTotal: String,
            templateModel: TemplateModel
        ) {
            val print_size = 6
            val error_level = 3
            when (templateModel.line_type) {
                "0" -> {
                    SunmiPrintHelper.getInstance().printSplitCut(context)
                }
                "1" -> {
                    SunmiPrintHelper.getInstance().printSplit1()
                }
                else -> {
                    SunmiPrintHelper.getInstance().printSplitSpace()
                }
            }
            SunmiPrintHelper.getInstance().setAlign(1)

            val details = templateModel.detail
            if (details.size > 0) {
                for (detail in details) {
                    var fontSize = 20f
                    if ("0".equals(detail.type)) {
                        fontSize = detail.text_font.toFloat()
                        SunmiPrintHelper.getInstance()
                            .printWithSize(detail.text_detail, fontSize * 1.6f)
                    }
                }
                SunmiPrintHelper.getInstance().printTitle("\nจำนวน $promotionTotal สิทธิ์")
                when (templateModel.type) {
                    "0" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance()
                                .printQr(templateModel.type_detail, print_size, error_level)
                        } else {
                            SunmiPrintHelper.getInstance().printQr(qr, print_size, error_level)
                        }
                        SunmiPrintHelper.getInstance().printSubtitleNoNewLine(qrText)
                    }
                    "1" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance()
                                .printBarcode(templateModel.type_detail)
                        } else {
//                            SunmiPrintHelper.getInstance().printBarcode("barcode dynamic")
                        }
                    }
                    "2" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance().printTitle(templateModel.type_detail)
                        }
                    }
                }
                SunmiPrintHelper.getInstance().printText("\n")
                SunmiPrintHelper.getInstance().setAlign(0)

                for (detail in details) {
                    var fontSize = 20f
                    if ("1".equals(detail.type)) {
                        fontSize = detail.text_font.toFloat()
                        SunmiPrintHelper.getInstance()
                            .printWithSize(detail.text_detail, fontSize * 1.5f)
                    }
                }
            } else {
                SunmiPrintHelper.getInstance().printTitle("จำนวน $promotionTotal สิทธิ์")

                when (templateModel.type) {
                    "0" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance()
                                .printQr(templateModel.type_detail, print_size, error_level)
                        } else {
                            SunmiPrintHelper.getInstance().printQr(qr, print_size, error_level)
                        }
                        SunmiPrintHelper.getInstance().printSubtitle(qrText)
                    }
                    "1" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance()
                                .printBarcode(templateModel.type_detail)
                        } else {
//                            SunmiPrintHelper.getInstance().printBarcode("1234")
                        }
                    }
                    "2" -> {
                        if (templateModel.type_detail != null && templateModel.type_detail.isNotEmpty()) {
                            SunmiPrintHelper.getInstance().printTitle(templateModel.type_detail)
                        }
                    }
                }
            }
        }
    }
}