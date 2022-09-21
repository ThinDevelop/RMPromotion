package com.rm.promotion.util

import android.content.Context
import com.rm.promotion.R
import com.rm.promotion.model.DateFormatConstant
import com.rm.promotion.model.GetReportResponseModel
import com.rm.promotion.toDate
import java.text.SimpleDateFormat
import java.util.*

class RMPrintUtil {

    companion object {
        fun printReport(reportModel: GetReportResponseModel, context: Context) {
            val summaryDate = reportModel.summary_date
            val promotions = reportModel.promotion
            var shift = reportModel.shift
//            val simpleDate = SimpleDateFormat(DateFormatConstant.yyyy_M_dd)
//            val date = simpleDate.format(summaryDate)
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
    }
}