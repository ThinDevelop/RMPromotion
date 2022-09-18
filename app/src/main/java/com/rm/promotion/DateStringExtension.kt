package com.rm.promotion

import android.text.format.DateUtils
import com.rm.promotion.model.DateFormatConstant
import com.rm.promotion.model.DateFormatConstant.yyyy_MM_dd_HH_mm_ss
import com.rm.promotion.model.DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_SSS_Z
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Number.toCurrency(digits: Int = 2, rounding: RoundingMode = RoundingMode.HALF_UP): String {
    val nf = NumberFormat.getNumberInstance()
        .apply {
            minimumFractionDigits = digits
            maximumFractionDigits = digits
            roundingMode = rounding
        }
    return nf.format(this)
}

fun String.convertDateToLong(dateFormat: String = yyyy_MM_dd_T_HH_mm_ss_SSS_Z): Long {
    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
    val timeZone = TimeZone.getDefault()
    simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)
    return simpleDateFormat.parse(this)?.time ?: 0L
}

fun String?.toDate(dateFormat: String = yyyy_MM_dd_HH_mm_ss): Date =
    try {
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        val date = simpleDateFormat.parse(this)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.time
    } catch (e: ParseException) {
        Date()
    } catch (e: NullPointerException) {
        Date()
    }

fun String?.toDateFromUTC(dateFormat: String = yyyy_MM_dd_T_HH_mm_ss_SSS_Z): Date =
    try {
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = simpleDateFormat.parse(this)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.time
    } catch (e: ParseException) {
        Date()
    } catch (e: NullPointerException) {
        Date()
    }

fun String?.toDateByDefaultTimeZone(dateFormat: String = yyyy_MM_dd_HH_mm_ss): Date =
    try {
        val simpleDateFormat = SimpleDateFormat(dateFormat)
        val timeZone = TimeZone.getDefault()
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)

        simpleDateFormat.parse(this) ?: Date()
    } catch (e: ParseException) {
        Date()
    } catch (e: NullPointerException) {
        Date()
    }

fun String?.changeFormatByDefaultTimeZone(
    dateFormat: String = yyyy_MM_dd_HH_mm_ss,
    resultFormat: String = yyyy_MM_dd_HH_mm_ss
): Date =
    try {
        val inputDateFormat = SimpleDateFormat(dateFormat)
        val timeZone = TimeZone.getDefault()
        inputDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)

        val outputDateFormat = SimpleDateFormat(resultFormat)
        val date = inputDateFormat.parse(this)
        val result = outputDateFormat.format(date)

        outputDateFormat.parse(result) ?: Date()
    } catch (e: ParseException) {
        Date()
    } catch (e: NullPointerException) {
        Date()
    }

fun String?.formatDateByDefaultTimeZone(
    dateFormat: String = yyyy_MM_dd_HH_mm_ss,
    resultFormat: String = yyyy_MM_dd_HH_mm_ss
): String =
    try {
        val simpleDateFormat = SimpleDateFormat(dateFormat)
        val timeZone = TimeZone.getDefault()
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)
        val resultFormatter = SimpleDateFormat(resultFormat)
        val data = simpleDateFormat.parse(this)

        resultFormatter.format(data).toString()
    } catch (e: ParseException) {
        ""
    } catch (e: NullPointerException) {
        ""
    }

fun String?.isToday(dateFormat: String = yyyy_MM_dd_T_HH_mm_ss_SSS_Z): Boolean {
    if (this == null) return false
    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
    val timeZone = TimeZone.getDefault()
    simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)

    val startDate = simpleDateFormat.parse(this)
    return DateUtils.isToday(startDate?.time ?: 0L)
}

fun String.checkNowDateIsBetweenStartDateAndEndDate(startDate: String): Boolean =
    try {
        val simpleDateFormat = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss, Locale.getDefault())
        val timeZone = TimeZone.getDefault()
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)

        val activeDate = simpleDateFormat.parse(startDate)
        val nowDate = Calendar.getInstance().time
        val endDate = simpleDateFormat.parse(this)

        activeDate.time < nowDate.time && nowDate.time < endDate.time
    } catch (e: ParseException) {
        false
    }

fun String.checkNowDateIsBeforeEndDate(): Boolean =
    try {
        val simpleDateFormat = SimpleDateFormat(yyyy_MM_dd_HH_mm_ss, Locale.getDefault())
        val timeZone = TimeZone.getDefault()
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)

        val nowDate = Calendar.getInstance().time
        val endDate = simpleDateFormat.parse(this)

        nowDate.time < endDate.time
    } catch (e: ParseException) {
        false
    }

/**
@Param date: String (ex: อา. 25 พ.ย. 2018)
 */
fun String.convertThaiYearFromString(): String {
    val dateList = this.replace(",", "").replace("-", " ").split(" ").toMutableList()

    return try {
        if (isNumeric(dateList.last())) {
            val year = dateList.last().toInt()
            if (year < 100) {
                dateList[dateList.lastIndex] = ((dateList.last().toInt() + 543) % 100).toString()
            } else {
                dateList[dateList.lastIndex] = (dateList.last().toInt() + 543).toString()
            }
            dateList.joinToString(" ")
        } else {
            this
        }
    } catch (e: Exception) {
        this
    }
}

fun isNumeric(number: String): Boolean {
    return number.matches("-?\\d+(\\.\\d+)?".toRegex())
}

fun String.convertTimeFormat(
    locale: Locale,
    format: String = DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss,
    formatResult: String = DateFormatConstant.dd_MMMM_yyyy
): String {
    val sdf = SimpleDateFormat(format, locale)
    val result: String
    try {
        val date = sdf.parse(this)
        sdf.applyPattern(formatResult)
        result = sdf.format(date ?: Date())
    } catch (e: ParseException) {
        return ""
    }
    return result
}

fun String?.fromUtcFormatTo(
    resultFormat: String = DateFormatConstant.yyyy_MM_dd_DASH,
    isThai: Boolean = false
): String {
    var result = ""

    try {
        val sdf = SimpleDateFormat(
            yyyy_MM_dd_T_HH_mm_ss_SSS_Z,
            if (isThai) Locale("th", "TH") else Locale("en", "EN")
        )
        sdf.timeZone = TimeZone.getTimeZone(DateFormatConstant.UTC)
        val date = sdf.parse(this)
        sdf.applyPattern(resultFormat)
        if (isThai) {
            sdf.timeZone = TimeZone.getDefault()
            val convertDate = sdf.format(Date(date.toString()))
            val dates = convertDate.split(" ").toTypedArray()
            dates[2] = (Integer.valueOf(dates[2]) + 543).toString() + "" // convert to year in thai
            for (date in dates) {
                result += date
                result += " "
            }
        } else {
            sdf.timeZone = TimeZone.getDefault()
            result = sdf.format(Date(date.toString()))
        }
        return result
    } catch (e: ParseException) {
        // For exception ,Will return Date() (current time on the device)
        return ""
    } catch (e: NullPointerException) {
        // For exception ,Will return Date() (current time on the device)
        return ""
    }
}
