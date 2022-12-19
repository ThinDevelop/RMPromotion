package com.rm.promotion.network

import android.util.Log
import com.google.gson.Gson
import com.rm.promotion.model.*
import com.rm.promotion.util.PreferenceUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException


class NetworkManager {

    companion object {

        private val URL_DOMAIN_DEV = "http://188.166.190.55"
        private val URL_DOMAIN_UAT = "http://175.176.222.91/api-ptt"
        private val URL_DOMAIN_PROD = "http://175.176.222.90/api-ptt"

        private val URL_DOMAIN = URL_DOMAIN_UAT
        private val URL_LOGIN = "$URL_DOMAIN/auth/login"
        private val URL_GET_DATA_MASTER = "$URL_DOMAIN/v2/station/master"
        private val URL_CLOSE_SHIFT = "$URL_DOMAIN/close/shift"
        private val URL_CLOSE_DATE = "$URL_DOMAIN/close/date"
        private val URL_REPORT = "$URL_DOMAIN/slip"

        private val STATUS_CODE_SUCCESS = 200
        private val STATUS_CODE_USER_NOT_FOUND = 401

        private fun loginOffline(username: String, password: String, listener: NetworkLoginLisener<LoginResponseModel>) {
            val users = PreferenceUtils.users
            if (users.isEmpty()) {
                listener.onError(
                    NetworkErrorModel(
                        "1150",
                        "User or Password incorrect"
                    )
                )
            }
            for (user in users) {
                if (user.username.equals(username) && user.password_mobile_pos.equals(password)) {
                    PreferenceUtils.token = ""
                    PreferenceUtils.currentUserId = user.id
                    PreferenceUtils.loginUserName = username
                    PreferenceUtils.loginPassword = password
                    PreferenceUtils.cashierName = user.name
                    PreferenceUtils.loginTime = System.currentTimeMillis().toString()
                    PreferenceUtils.setLoginSuccess()

                    listener.onResponseOffline()
                    return
                }
            }

            listener.onError(
                NetworkErrorModel(
                    "1150",
                    "User or Password incorrect"
                )
            )
        }

        fun loginOk( username: String,
                     password: String,
                     listener: NetworkLoginLisener<LoginResponseModel>) {
            val client = OkHttpClient()

            val obj = JSONObject()
            obj.put("serialNumber", android.os.Build.SERIAL)
            obj.put("username", username)
            obj.put("password", password)
            obj.put("loginType", "1")
            obj.put("shift", PreferenceUtils.preferenceKeyCurrentShift)
            obj.put("business", PreferenceUtils.preferenceKeyBusinessDate)
            val mediaType = "application/json; charset=utf-8"
            val request: Request = Request.Builder()
                .url(URL_LOGIN)
                .addHeader("Content-Type", "application/json")
                .post(obj.toString().toRequestBody(mediaType.toMediaType()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.let {
                        it.printStackTrace()
                    }
                    loginOffline(username, password, listener)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val data = response.body?.string()
                        Log.d("login response ", "data : " + data)

                        val jsonObject = JSONObject(data)
                        jsonObject.let {
                            Log.d("login response", response.toString())
                            var status = 0
                            if (it.has("statusCode")) {
                                status = it.getInt("statusCode")
                            } else if (it.has("status")) {
                                status = it.getInt("status")
                            }

                            if (STATUS_CODE_SUCCESS == status) {
                                PreferenceUtils.setLoginSuccess()
                                val data = it.getJSONObject("data")
                                val loginResponseModel =
                                    Gson().fromJson(data.toString(), LoginResponseModel::class.java)
                                val users = data.optJSONArray("users")
                                val products = data.optJSONArray("products")
                                val shiftObj = data.optJSONObject("shift")
                                val businessDate = shiftObj.optString("date", "")
                                val station = data.optJSONObject("station")
                                val stationId = station.optString("id")
                                val stationCode = station.optString("code")
                                val stationName = station.optString("name_th")
                                val shift = shiftObj.optString("shift", "")
                                val profile = data.optJSONObject("profile")
                                val userId = profile.optString("id", "")
                                val token = it.getString("token")
                                val name = loginResponseModel.profile.name
                                val username = loginResponseModel.profile.username
                                val password = loginResponseModel.profile.password_mobile_pos
                                PreferenceUtils.token = token
                                PreferenceUtils.currentUserId = userId
                                PreferenceUtils.loginUserName = username
                                PreferenceUtils.loginPassword = password
                                PreferenceUtils.cashierName = name
                                PreferenceUtils.loginTime = System.currentTimeMillis().toString()
                                PreferenceUtils.setUsers(users?.toString())
//                            PreferenceUtils.setProducts(products?.toString())
                                PreferenceUtils.stationId = stationId
                                PreferenceUtils.stationCode = stationCode
                                PreferenceUtils.stationName = stationName

                                listener.onResponse(loginResponseModel)
                            } else if (STATUS_CODE_USER_NOT_FOUND == status) {
                                listener.onError(
                                    NetworkErrorModel(
                                        "1150",
                                        "User or Password incorrect"
                                    )
                                )
                            } else {
                                loginOffline(username, password, listener)
                            }
                        }
                    } else {
                        listener.onError(
                            NetworkErrorModel(
                                response.code.toString(),
                                response.message
                            )
                        )
                    }
                }
            })
        }

        fun getDataMasterOk(listener: NetworkLisener<RMDataModel>) {
            val client = OkHttpClient()
            val obj = JSONObject()
            obj.put("mobile_pos_id", android.os.Build.SERIAL)
            obj.put("station_id", PreferenceUtils.stationId)
            val mediaType = "application/json; charset=utf-8"
            val request: Request = Request.Builder()
                .url(URL_GET_DATA_MASTER)
                .addHeader("Authorization", "Bearer " + PreferenceUtils.token)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(obj.toString().toRequestBody(mediaType.toMediaType()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e?.let {
                        it.printStackTrace()
                        listener.onError(
                            NetworkErrorModel(
                                "500",
                                it.message.toString()
                            )
                        )
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val data = response.body?.string()
                        val jsonObject = JSONObject(data)
                        jsonObject?.let {
                            var status = 0
                            if (it.has("statusCode")) {
                                status = it.getInt("statusCode")
                            } else if (it.has("status")) {
                                status = it.getInt("status")
                            }
                            if (STATUS_CODE_SUCCESS == status) {
                                val data = it.getJSONObject("data")
                                val promotion = data.getJSONArray("promotions")
                                val users = data.optJSONArray("users")
                                val products = data.optJSONArray("products")
                                val station = data.optJSONObject("station")
                                val stationId = station.optString("id")
                                val stationName = station.optString("name_th")
                                PreferenceUtils.stationId = stationId
                                PreferenceUtils.stationName = stationName
                                for (i in 0 until promotion.length()) {
                                    val promo = promotion.getJSONObject(i)
                                    if (promo.has("templates")) {
                                        val templateData = promo.get("templates").toString()
                                        val json = JSONTokener(templateData).nextValue();
                                        if (json is JSONObject) {
                                            val array = JSONArray()
                                            array.put(json)
                                            promo.remove("templates")
                                            promo.put("templates", array)
                                        }
                                    }
                                    if (promo.has("conditions")) {
                                        val conditionsData = promo.get("conditions").toString()
                                        val json = JSONTokener(conditionsData).nextValue();
                                        if (json is JSONObject) {
                                            val array = JSONArray()
                                            array.put(json)
                                            promo.remove("conditions")
                                            promo.put("conditions", array)
                                        }
                                    }
                                }

                                PreferenceUtils.setPromotion(promotion.toString())
                                PreferenceUtils.setUsers(users?.toString())
                                PreferenceUtils.setProducts(products?.toString())
                                val rmDataModel =
                                    Gson().fromJson(data.toString(), RMDataModel::class.java)


                                listener.onResponse(rmDataModel)
                            } else {
                                listener.onError(
                                    NetworkErrorModel(
                                        status.toString(),
                                        it.getString("message")
                                    )
                                )
                            }
                        }
                    } else {
                        listener.onError(
                            NetworkErrorModel(
                                response.code.toString(),
                                response.message
                            )
                        )
                    }
                }
            })
        }

        fun closeShiftOk(obj: JSONObject, listener: NetworkLisener<CloseShiftResponseModel>) {
            val client = OkHttpClient()
            val mediaType = "application/json; charset=utf-8"
            val request: Request = Request.Builder()
                .url(URL_CLOSE_SHIFT)
                .addHeader("Authorization", "Bearer " + PreferenceUtils.token)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(obj.toString().toRequestBody(mediaType.toMediaType()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e?.let {
                        it.printStackTrace()
                        listener.onError(
                            NetworkErrorModel(
                                "500",
                                e.message.toString()
                            )
                        )
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val data = response.body?.string()
                        val jsonObject = JSONObject(data)
                        jsonObject?.let {
                            Log.d("closeShift response", it.toString())
                            var status = 0
                            if (it.has("statusCode")) {
                                status = it.getInt("statusCode")
                            } else if (it.has("status")) {
                                status = it.getInt("status")
                            }
                            if (STATUS_CODE_SUCCESS == status) {
                                val data = it.getJSONObject("data").toString()
                                val closeShiftResponseModel = Gson().fromJson(data, CloseShiftResponseModel::class.java)
                                listener.onResponse(closeShiftResponseModel)
                            } else {
                                listener.onError(
                                    NetworkErrorModel(
                                        status.toString(),
                                        it.getString("message")
                                    )
                                )
                            }
                        }
                    } else {
                        listener.onError(
                            NetworkErrorModel(
                                response.code.toString(),
                                response.message
                            )
                        )
                    }
                }
            })
        }

        fun getReportOk(summaryDate: String, shift: String, listener: NetworkLisener<GetReportResponseModel>) {
            val client = OkHttpClient()
            val obj = JSONObject()
            obj.put("mobile_pos_id", android.os.Build.SERIAL)
            obj.put("summary_date", summaryDate)
            obj.put("shift", shift)
            val mediaType = "application/json; charset=utf-8"
            val request: Request = Request.Builder()
                .url(URL_REPORT)
                .addHeader("Authorization", "Bearer " + PreferenceUtils.token)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(obj.toString().toRequestBody(mediaType.toMediaType()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e?.let {
                        it.printStackTrace()
                        listener.onError(
                            NetworkErrorModel(
                                "500",
                                e.message.toString()
                            )
                        )
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val data = response.body?.string()
                        Log.d("login response ", "data : " + data)
                        val jsonObject = JSONObject(data)
                        jsonObject?.let {
                            var status = 0
                            if (it.has("statusCode")) {
                                status = it.getInt("statusCode")
                            } else if (it.has("status")) {
                                status = it.getInt("status")
                            }
                            if (STATUS_CODE_SUCCESS == status) {
                                val data = it.getJSONObject("data").toString()
                                val closeShiftResponseModel = Gson().fromJson(data, GetReportResponseModel::class.java)
                                listener.onResponse(closeShiftResponseModel)
                            } else {
                                listener.onError(
                                    NetworkErrorModel(
                                        status.toString(),
                                        it.getString("message")
                                    )
                                )
                            }
                        }
                    } else {
                        listener.onError(
                            NetworkErrorModel(
                                response.code.toString(),
                                response.message
                            )
                        )
                    }
                }
            })
        }

        interface NetworkLoginLisener<T> {
            fun onResponse(response: T)
            fun onResponseOffline()
            fun onError(errorModel: NetworkErrorModel)
            fun onExpired()
        }

        interface NetworkLisener<T> {
            fun onResponse(response: T)
            fun onError(errorModel: NetworkErrorModel)
            fun onExpired()
        }
    }
}