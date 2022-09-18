package com.rm.promotion.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.google.gson.Gson
import com.rm.promotion.model.Login
import com.rm.promotion.model.SlipModel
import com.rm.promotion.model.TransactionModel
import org.json.JSONObject
import java.io.*

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
                        login = mutableListOf(login)
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
                        login = mutableListOf(login)
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