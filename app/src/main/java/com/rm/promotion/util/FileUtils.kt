package com.rm.promotion.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.google.gson.Gson
import com.rm.promotion.model.*
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class FileUtils {

    companion object {

        fun saveData() {

        }

        fun saveJsonFile(context: Context, slipModel: SlipModel) {
            if (!isExternalStorageWritable()) {
                Toast.makeText(context, "isExternalStorageWritable is false", Toast.LENGTH_LONG).show()
            } else {
                val file = File(context.getExternalFilesDir("data"), "data_shift.json")
                if (file.exists()) {
                    val orgData = getJsonObjectFromFile(context)
                    val transactionModel = Gson().fromJson(orgData.toString(), TransactionModel::class.java)
                    transactionModel.slip.apply {
                        add(slipModel)
                    }
                    val json = Gson().toJson(transactionModel)
                    writeFile(context, file, JSONObject(json))
                } else {
                    val login = Login(
                        login_time = System.currentTimeMillis().toString(),
                        business_date = PreferenceUtils.preferenceKeyBusinessDate,
                        shift = PreferenceUtils.preferenceKeyCurrentShift,
                        user_id = PreferenceUtils.currentUserId
                    )
                    val transactionModel = TransactionModel(
                        station_id = PreferenceUtils.stationId,
                        mobile_pos_id = android.os.Build.SERIAL,
                        business_date = PreferenceUtils.preferenceKeyBusinessDate,
                        user_id = PreferenceUtils.currentUserId,
                        shift = PreferenceUtils.preferenceKeyCurrentShift,
                        slip = mutableListOf(slipModel),
                        login = mutableListOf(login),
                        shift_data = mutableListOf()
                    )
                    val json = Gson().toJson(transactionModel)
                    writeFile(context, file, JSONObject(json))
                }

            }
        }

        fun saveNewBusiness(context: Context, shift: Shift) {
            if (!isExternalStorageWritable()) {
                Toast.makeText(context, "isExternalStorageWritable is false", Toast.LENGTH_LONG).show()
            } else {
                val file = File(context.getExternalFilesDir("data"), "data_shift.json")
                if (file.exists()) {
                    val orgData = getJsonObjectFromFile(context)
                    val transactionModel = Gson().fromJson(orgData.toString(), TransactionModel::class.java)
                    transactionModel.shift_data.apply {
                        add(shift)
                    }
                    val json = Gson().toJson(transactionModel)
                    writeFile(context, file, JSONObject(json))
                } else {
                    val transactionModel = TransactionModel(
                        station_id = PreferenceUtils.stationId,
                        mobile_pos_id = android.os.Build.SERIAL,
                        business_date = PreferenceUtils.preferenceKeyBusinessDate,
                        user_id = PreferenceUtils.currentUserId,
                        shift = PreferenceUtils.preferenceKeyCurrentShift,
                        slip = mutableListOf(),
                        login = mutableListOf(),
                        shift_data = mutableListOf(shift)
                    )
                    val json = Gson().toJson(transactionModel)
                    writeFile(context, file, JSONObject(json))
                }
            }
        }
        fun saveLogin(context: Context, login: Login) {
            if (!isExternalStorageWritable()) {
                Toast.makeText(context, "isExternalStorageWritable is false", Toast.LENGTH_LONG).show()
            } else {
                val file = File(context.getExternalFilesDir("data"), "data_shift.json")
                if (file.exists()) {
                    val orgData = getJsonObjectFromFile(context)
                    val transactionModel = Gson().fromJson(orgData.toString(), TransactionModel::class.java)
                    transactionModel.login.apply {
                        add(login)
                    }
                    val json = Gson().toJson(transactionModel)
                    writeFile(context, file, JSONObject(json))
                } else {
                    val transactionModel = TransactionModel(
                        station_id = PreferenceUtils.stationId,
                        mobile_pos_id = android.os.Build.SERIAL,
                        business_date = PreferenceUtils.preferenceKeyBusinessDate,
                        user_id = PreferenceUtils.currentUserId,
                        shift = PreferenceUtils.preferenceKeyCurrentShift,
                        slip = mutableListOf(),
                        login = mutableListOf(login),
                        shift_data = mutableListOf()
                    )
                    val json = Gson().toJson(transactionModel)
                    writeFile(context, file, JSONObject(json))
                }
            }
        }

        fun deleteJsonObjectFromFile(context: Context): Boolean {
            val file = File(context.getExternalFilesDir("data"), "data_shift.json")
            if (!file.exists()) return true
            return file.delete()
        }

        fun fileIsExists(context: Context): Boolean {
            val file = File(context.getExternalFilesDir("data"), "data_shift.json")
            return file.exists()
        }

        fun getFile(context: Context): TransactionModel {
            val orgData = getJsonObjectFromFile(context)
            return Gson().fromJson(orgData.toString(), TransactionModel::class.java)
        }

        fun setEndDate(context: Context, close_date: Boolean = false) {
            if (!isExternalStorageWritable()) {
                Toast.makeText(context, "isExternalStorageWritable is false", Toast.LENGTH_LONG).show()
            } else {
                val file = File(context.getExternalFilesDir("data"), "data_shift.json")
                if (file.exists()) {
                    val orgData = getJsonObjectFromFile(context)
                    val transactionModel = Gson().fromJson(orgData.toString(), TransactionModel::class.java)
                    transactionModel.shift_data.filter { it.end_date.isEmpty() }.first().apply {
                        val sdf = SimpleDateFormat(
                            DateFormatConstant.yyyy_MM_dd_HH_mm_ss,
                            Locale.getDefault()
                        )
                        this.end_date = sdf.format(Date())
                        this.close_date = close_date
                    }
                    val json = Gson().toJson(transactionModel)
                    writeFile(context, file, JSONObject(json))
                }
            }
        }

        fun getJsonObjectFromFile(context: Context): JSONObject {
            val file = File(context.getExternalFilesDir("data"), "data_shift.json")
            if (!file.exists()) return JSONObject()
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            val responce = stringBuilder.toString()

            return JSONObject(responce)
        }

        fun addSlip(context: Context, summary: PromotionSummary) {
            addSummaryData(context, summary)
            addSummaryShift(context, summary)
        }

        fun addSummaryData(context: Context, summary: PromotionSummary) {
            if (!isExternalStorageWritable()) {
                Toast.makeText(context, "isExternalStorageWritable is false", Toast.LENGTH_LONG).show()
            } else {
                val file = File(context.getExternalFilesDir("data"), "data_summary_date.json")
                if (file.exists()) {
                    val orgData = getSummaryDateJsonObjectFromFile(context)
                    val summaryModel = Gson().fromJson(orgData.toString(), SummaryDate::class.java)
                    summaryModel.apply {
                        this.shift = PreferenceUtils.preferenceKeyCurrentShift
                        this.summary_date = PreferenceUtils.preferenceKeyBusinessDate
                        var added = false
                        var hasPromotion = false
                        this.promotion.forEach {
                            var number = ""
                            var slip = ""
                            var sumSlip = 0
                            if (summary.summary_slips.isNotEmpty()) {
                                number = summary.summary_slips.first().numbers
                                slip = summary.summary_slips.first().slips
                            }

                            if (it.promotion_code.equals(summary.promotion_code)) {
                                hasPromotion = true
                                it.summary_slips.forEach { slips->
                                    if (slips.numbers.equals(number)) {
                                        val slipBase = slips.slips.toInt()
                                        sumSlip = slip.toInt()+slipBase
                                        slips.slips = sumSlip.toString()
                                        added = true
                                        return@forEach
                                    }
                                }
                                if (!added) {
                                    it.summary_slips.add(SummarySlips(number, slip))
                                }
                                return@forEach
                            }
                        }
                        if (!hasPromotion) {
                            this.promotion.add(summary)
                        }
                    }
                    val json = Gson().toJson(summaryModel)
                    writeFile(context, file, JSONObject(json))
                } else {
                    val summaryShift = SummaryShift(
                        PreferenceUtils.preferenceKeyBusinessDate,
                        PreferenceUtils.preferenceKeyCurrentShift,
                        arrayListOf(summary)
                    )
                    val json = Gson().toJson(summaryShift)
                    writeFile(context, file, JSONObject(json))
                }
            }
        }

        fun getSummaryDateJsonObjectFromFile(context: Context): JSONObject {
            val file = File(context.getExternalFilesDir("data"), "data_summary_date.json")
            if (!file.exists()) return JSONObject()
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            val responce = stringBuilder.toString()

            return JSONObject(responce)
        }

        fun deleteSummaryDay(context: Context): Boolean {
            val file = File(context.getExternalFilesDir("data"), "data_summary_date.json")
            if (!file.exists()) return true
            return file.delete()
        }

        fun deleteSummaryShift(context: Context): Boolean {
            val file = File(context.getExternalFilesDir("data"), "data_summary_shift.json")
            if (!file.exists()) return true
            return file.delete()
        }

        fun addSummaryShift(context: Context, summary: PromotionSummary) {
            if (!isExternalStorageWritable()) {
                Toast.makeText(context, "isExternalStorageWritable is false", Toast.LENGTH_LONG).show()
            } else {
                val file = File(context.getExternalFilesDir("data"), "data_summary_shift.json")
                if (file.exists()) {
                    val orgData = getSummaryShiftJsonObjectFromFile(context)
                    val summaryModel = Gson().fromJson(orgData.toString(), SummaryShift::class.java)

                    summaryModel.apply {
                        this.shift = PreferenceUtils.preferenceKeyCurrentShift
                        this.summary_date = PreferenceUtils.preferenceKeyBusinessDate
                        var added = false
                        var hasPromotion = false
                        this.promotion.forEach {
                            var number = ""
                            var slip = ""
                            var sumSlip = 0
                            if (summary.summary_slips.isNotEmpty()) {
                                number = summary.summary_slips.first().numbers
                                slip = summary.summary_slips.first().slips
                            }

                            if (it.promotion_code.equals(summary.promotion_code)) {
                                hasPromotion = true
                                it.summary_slips.forEach { slips->
                                    if (slips.numbers.equals(number)) {
                                        val slipBase = slips.slips.toInt()
                                        sumSlip = slip.toInt()+slipBase
                                        slips.slips = sumSlip.toString()
                                        added = true
                                        return@forEach
                                    }
                                }
                                if (!added) {
                                    it.summary_slips.add(SummarySlips(number, slip))
                                }
                                return@forEach
                            }
                        }
                        if (!hasPromotion) {
                            this.promotion.add(summary)
                        }
                    }
                    val json = Gson().toJson(summaryModel)
                    writeFile(context, file, JSONObject(json))
                } else {
                    val summaryShift = SummaryShift(
                        PreferenceUtils.preferenceKeyBusinessDate,
                        PreferenceUtils.preferenceKeyCurrentShift,
                        arrayListOf(summary)
                    )
                    val json = Gson().toJson(summaryShift)
                    writeFile(context, file, JSONObject(json))
                }
            }
        }

        fun getSummaryShiftJsonObjectFromFile(context: Context): JSONObject {
            val file = File(context.getExternalFilesDir("data"), "data_summary_shift.json")
            if (!file.exists()) return JSONObject()
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }
            bufferedReader.close()
            val responce = stringBuilder.toString()

            return JSONObject(responce)
        }


        private fun writeFile(context: Context, file: File, obj: JSONObject) {
            try {
                var output: Writer? = null
                output = BufferedWriter(FileWriter(file))
                output.write(obj.toString())
                output.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /* Checks if external storage is available for read and write */
        fun isExternalStorageWritable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }
    }
}