package com.rm.promotion.util

import android.content.Context
import com.rm.promotion.R
import com.rm.promotion.model.DateFormatConstant
import com.rm.promotion.model.GetReportResponseModel
import java.text.SimpleDateFormat

class RMPrintUtil {

    companion object {
        fun printReport(reportModel: GetReportResponseModel, context: Context) {
            val summaryDate = reportModel.summary_date
            val promotions = reportModel.promotion
            val simpleDate = SimpleDateFormat(DateFormatConstant.dd_MM_yyyy_HH_mm_ss)
            val date = simpleDate.format(summaryDate)
            val width = intArrayOf(1, 1)
            val align = intArrayOf(0, 2)
            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printTitle("\n\n" +PreferenceUtils.stationName)
            SunmiPrintHelper.getInstance().printSubtitle(PreferenceUtils.stationId)
            SunmiPrintHelper.getInstance().printSubtitle(PreferenceUtils.cashierName)
            SunmiPrintHelper.getInstance().printSubtitle(PreferenceUtils.currentUserId)
            SunmiPrintHelper.getInstance().printSubtitle("ใบสรุปยอดวันที่#$date กะ1")
            SunmiPrintHelper.getInstance().printSplit2()

            val header = arrayOf(context.getString(R.string.header_promotion), context.getString(R.string.header_summary))
            SunmiPrintHelper.getInstance().printTable(header, width, align)
            SunmiPrintHelper.getInstance().setAlign(0)
            for (promotion in promotions) {
                val promotionId = promotion.promotion_code
                SunmiPrintHelper.getInstance().printSubtitle("$promotion รหัส $promotionId")
                for (slip in promotion.summary_slips) {
                    val slipNumber = slip.slips
                    val promotionNumber = slip.numbers
                    val slipDetail = arrayOf("จำนวน $promotionNumber สิทธิ์", "พิมพ์ $slipNumber ใบ")
                    SunmiPrintHelper.getInstance().printTable(slipDetail, width, align)
                }
            }

            SunmiPrintHelper.getInstance().setAlign(1)
            SunmiPrintHelper.getInstance().printSplit2()
            SunmiPrintHelper.getInstance().printSubtitle("1365 Contact Center")
            SunmiPrintHelper.getInstance().printSubtitle("==VAT INCLUDED==")
            SunmiPrintHelper.getInstance().printSubtitle("THANK YOU AND WELCOME")
        }
    }
}