package com.rm.promotion.util

import com.rm.promotion.model.DateFormatConstant
import java.text.SimpleDateFormat
import java.util.*

class TimeUtil {

    companion object {
        fun getDate(date: String): Date {
            val sdf = SimpleDateFormat(DateFormatConstant.yyyy_M_dd, Locale.getDefault())
            val c: Calendar = Calendar.getInstance()
            c.setTime(sdf.parse(date))
            return c.time
        }

        fun getNowDate(): Date {
            val sdf = SimpleDateFormat(DateFormatConstant.yyyy_M_dd, Locale.getDefault())
            val systemDate = Calendar.getInstance().time
            val nowStr = sdf.format(systemDate)
            return getDate(nowStr)
        }

        fun getStartDate(date: Date): String {
            val sdfStart = SimpleDateFormat(
                DateFormatConstant.yyyy_MM_dd_HH_mm_ss,
                Locale.getDefault()
            )
            return sdfStart.format(date)
        }

        fun getEndDate(date: String): Date {
            val sdf = SimpleDateFormat(DateFormatConstant.yyyy_MM_dd_HH_mm_ss, Locale.getDefault())
            val c: Calendar = Calendar.getInstance()
            c.setTime(sdf.parse(date))
            return c.time
        }
    }
}