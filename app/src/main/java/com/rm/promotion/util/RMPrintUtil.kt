package com.rm.promotion.util

import android.content.Context
import android.widget.Toast
import com.rm.promotion.R
import com.rm.promotion.model.*
import com.rm.promotion.toDate
import java.text.SimpleDateFormat
import java.util.*

class RMPrintUtil {

    companion object {

        fun printReportDay(context: Context, reportModel: SummaryDate) {
            val summaryDate = reportModel.summary_date
            val promotions = reportModel.promotion
            var shift = ""

            val dateFormated = summaryDate.toDate(DateFormatConstant.yyyy_M_dd)
            val simpleDate = SimpleDateFormat(DateFormatConstant.dd_M_yyyy, Locale.getDefault())
            val date = simpleDate.format(dateFormated)

            val width = intArrayOf(1, 1)
            val align = intArrayOf(0, 2)
            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printTitle("\n\n" +PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle("Station ID "+ PreferenceUtils.stationId)
            SunmiPrintHelper.getInstance().printSubtitle("Cashier Name "+ PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance().printSubtitle("Cashier ID "+ PreferenceUtils.currentUserId)
            SunmiPrintHelper.getInstance().printSubtitle("ใบสรุปยอดวันที่#$date $shift")
            SunmiPrintHelper.getInstance().printSplit2()

            val header = arrayOf(context.getString(R.string.header_promotion), context.getString(R.string.header_summary))
            SunmiPrintHelper.getInstance().printTable(header, width, align, false)
            SunmiPrintHelper.getInstance().setAlign(0)
            for (promotion in promotions) {
                val promotionId = promotion.promotion_code
                val promotionName = promotion.promotion_name
                SunmiPrintHelper.getInstance().printSubtitle("$promotionName รหัส $promotionId")
                for (slip in promotion.summary_slips) {
                    val slipNumber = slip.slips
                    val promotionNumber = slip.numbers
                    val slipDetail = arrayOf("จำนวน $promotionNumber สิทธิ์", "พิมพ์ $slipNumber ใบ")
                    SunmiPrintHelper.getInstance().printTable(slipDetail, width, align,false)
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
            SunmiPrintHelper.getInstance().printTitle("\n\n" +PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle("Station ID "+ PreferenceUtils.stationId)
            SunmiPrintHelper.getInstance().printSubtitle("Cashier Name "+ PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance().printSubtitle("Cashier ID "+ PreferenceUtils.currentUserId)
            SunmiPrintHelper.getInstance().printSubtitle("ใบสรุปยอดวันที่#$date $shift")
            SunmiPrintHelper.getInstance().printSplit2()

            val header = arrayOf(context.getString(R.string.header_promotion), context.getString(R.string.header_summary))
            SunmiPrintHelper.getInstance().printTable(header, width, align, false)
            SunmiPrintHelper.getInstance().setAlign(0)
            for (promotion in promotions) {
                val promotionId = promotion.promotion_code
                val promotionName = promotion.promotion_name
                SunmiPrintHelper.getInstance().printSubtitle("$promotionName รหัส $promotionId")
                for (slip in promotion.summary_slips) {
                    val slipNumber = slip.slips
                    val promotionNumber = slip.numbers
                    val slipDetail = arrayOf("จำนวน $promotionNumber สิทธิ์", "พิมพ์ $slipNumber ใบ")
                    SunmiPrintHelper.getInstance().printTable(slipDetail, width, align,false)
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
            SunmiPrintHelper.getInstance().printTitle("\n\n" +PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle("Station ID "+ PreferenceUtils.stationId)
            SunmiPrintHelper.getInstance().printSubtitle("Cashier Name "+ PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance().printSubtitle("Cashier ID "+ PreferenceUtils.currentUserId)
            SunmiPrintHelper.getInstance().printSubtitle("ใบสรุปยอดวันที่#$date $shift")
            SunmiPrintHelper.getInstance().printSplit2()

            val header = arrayOf(context.getString(R.string.header_promotion), context.getString(R.string.header_summary))
            SunmiPrintHelper.getInstance().printTable(header, width, align, false)
            SunmiPrintHelper.getInstance().setAlign(0)
            for (promotion in promotions) {
                val promotionId = promotion.promotion_code
                val promotionName = promotion.promotion_name
                SunmiPrintHelper.getInstance().printSubtitle("$promotionName รหัส $promotionId")
                for (slip in promotion.summary_slips) {
                    val slipNumber = slip.slips
                    val promotionNumber = slip.numbers
                    val slipDetail = arrayOf("จำนวน $promotionNumber สิทธิ์", "พิมพ์ $slipNumber ใบ")
                    SunmiPrintHelper.getInstance().printTable(slipDetail, width, align,false)
                }
            }

            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printSplit2()
            SunmiPrintHelper.getInstance().printSubtitle("1365 Contact Center")
            SunmiPrintHelper.getInstance().printSubtitle("==VAT INCLUDED==")
            SunmiPrintHelper.getInstance().printSubtitle("THANK YOU AND WELCOME\n\n")
            SunmiPrintHelper.getInstance().feedPaper()
        }


        fun printPromotion(context: Context, templateModel: TemplateModel, createdAt: Date, productName: String, price: String, qr: String, qrText: String, promotionTotal: String) {
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
            SunmiPrintHelper.getInstance().printTitle("\n\n" +PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle(PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance().printSubtitle("วันที่ขาย#" + PreferenceUtils.getBusinessDate())//currentDate)
            SunmiPrintHelper.getInstance().printSplit2()

            SunmiPrintHelper.getInstance().printTable(productName, width, align)
            SunmiPrintHelper.getInstance().printTable(total, width, align)
            SunmiPrintHelper.getInstance().printTable(cash, width, align)
            SunmiPrintHelper.getInstance().printSplit2()
            SunmiPrintHelper.getInstance().printSubtitle("1365 Contact Center")
            SunmiPrintHelper.getInstance().printSubtitle("==VAT INCLUDED==")
            SunmiPrintHelper.getInstance().printSubtitle("THANK YOU AND WELCOME")

            when (templateModel.line_type) {
                "0"->{SunmiPrintHelper.getInstance().printSplitCut(context)}
                "1"->{SunmiPrintHelper.getInstance().printSplit1()}
                else->{SunmiPrintHelper.getInstance().printSplitSpace()}
            }
            SunmiPrintHelper.getInstance().setAlign(1)

            val details = templateModel.detail
            if (details.size > 0) {
                var headLineCount = 0
                for (detail in details) {
                    var fontSize = 20f
                    if ("0".equals(detail.type)) {
                        headLineCount++
                        try {
                            fontSize = detail.text_font.toFloat()
                        } catch (e: Exception) {

                        }
                        when (headLineCount) {
                            1,2,3 -> fontSize = 14f
                        }
                        SunmiPrintHelper.getInstance().printWithSize(detail.text_detail, fontSize*1.8f)
                    }
                }
                SunmiPrintHelper.getInstance().printTitle("จำนวน $promotionTotal สิทธิ์")
                SunmiPrintHelper.getInstance().printQr(qr, print_size, error_level)
                SunmiPrintHelper.getInstance().printSubtitle(qrText)
                SunmiPrintHelper.getInstance().setAlign(0)

                var footLineCount = 0
                for (detail in details) {
                    var fontSize = 20f
                    if ("1".equals(detail.type)) {
                        footLineCount++
                        try {
                            fontSize = detail.text_font.toFloat()
                        } catch (e: Exception) {

                        }

                        when (footLineCount) {
                            1 -> fontSize = 14f
                            else -> fontSize = 9f
                        }

                        SunmiPrintHelper.getInstance().printWithSize(detail.text_detail, fontSize*1.5f)
                    }
                }
            } else {
                SunmiPrintHelper.getInstance().printTitle("จำนวน $promotionTotal สิทธิ์")
                SunmiPrintHelper.getInstance().printQr(qr, print_size, error_level)
                SunmiPrintHelper.getInstance().printSubtitle(qrText)
            }
            SunmiPrintHelper.getInstance().feedPaper()
        }
    }
}