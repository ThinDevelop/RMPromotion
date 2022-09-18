package com.rm.promotion.network

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.Gson
import com.rm.promotion.model.*
import com.rm.promotion.toDate
import com.rm.promotion.util.FileUtils
import com.rm.promotion.util.PreferenceUtils
import org.json.JSONObject

class NetworkManager {

    companion object {
        private val URL_DOMAIN = "http://188.166.190.55"
        private val URL_LOGIN = "$URL_DOMAIN/auth/login"
        private val URL_GET_DATA_MASTER = "$URL_DOMAIN/station"
        private val URL_CLOSE_SHIFT = "$URL_DOMAIN/close/shift"
        private val URL_CLOSE_DATE = "$URL_DOMAIN/close/date"
        private val URL_REPORT = "$URL_DOMAIN/slip"

        private val STATUS_CODE_SUCCESS = 200


        fun login(
            username: String,
            password: String,
            listener: NetworkLoginLisener<LoginResponseModel>) {
            AndroidNetworking.post(URL_LOGIN)
                .addHeaders("Content-Type", "application/json")
                .addBodyParameter("serialNumber", android.os.Build.SERIAL)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .addBodyParameter("loginType", "1")
                .addBodyParameter("shift", PreferenceUtils.preferenceKeyCurrentShift)
                .addBodyParameter("business", PreferenceUtils.preferenceKeyBusinessDate)
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        response?.let {
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
                                val loginResponseModel = Gson().fromJson(data.toString(), LoginResponseModel::class.java)
                                val users = data.optJSONArray("users")
                                val products = data.optJSONArray("products")
                                val shiftObj = data.optJSONObject("shift")
                                val businessDate = shiftObj.optString("date", "")
                                val station = data.optJSONObject("station")
                                val stationId = station.optString("id")
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
                                PreferenceUtils.setProducts(products?.toString())
                                PreferenceUtils.preferenceKeyCurrentShift = shift
                                PreferenceUtils.preferenceKeyBusinessDate = businessDate
                                PreferenceUtils.stationId = stationId
                                PreferenceUtils.stationName = stationName

                                listener.onResponse(loginResponseModel)
                            } else {
                                loginOffline(username, password, listener)
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anError?.let {
                            it.printStackTrace()
                        }
                        loginOffline(username, password, listener)
                    }
                })
        }

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

        fun getDataMaster(stationId: String, listener: NetworkLisener<RMDataModel>) {
            AndroidNetworking.get(URL_GET_DATA_MASTER+"/"+stationId+"/master")
                .addHeaders("Authorization", "Bearer " + PreferenceUtils.token)
                .addHeaders("Content-type", "application/json")
                .addHeaders("Accept", "application/json")
                .setTag("getDataMaster")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        Log.d("getDataMaster response", response.toString())

                        response?.let {
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
                                PreferenceUtils.setPromotion(promotion.toString())
                                PreferenceUtils.setUsers(users?.toString())
                                PreferenceUtils.setProducts(products?.toString())
                                val rmDataModel = Gson().fromJson(data.toString(), RMDataModel::class.java)


                                listener.onResponse(rmDataModel)
                            } else {
                                val error = ANError(it.getString("message"))
                                error.errorCode = status
                                listener.onError(
                                    NetworkErrorModel(
                                        error.errorCode.toString(),
                                        error.message.toString()
                                    )
                                )
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anError?.let {
                            it.printStackTrace()
                            listener.onError(
                                NetworkErrorModel(
                                    anError.errorCode.toString(),
                                    anError.message.toString()
                                )
                            )
                        }
                    }
                })
        }

        fun closeShift(obj: JSONObject, listener: NetworkLisener<CloseShiftResponseModel>) {

            AndroidNetworking.post(URL_CLOSE_SHIFT)
                .addHeaders("Authorization", "Bearer " + PreferenceUtils.token)
                .addHeaders("Content-type", "application/json")
                .addJSONObjectBody(obj)
                .setTag("closeShift")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        response?.let {
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
                                val error = ANError(it.getString("message"))
                                error.errorCode = status
                                listener.onError(
                                    NetworkErrorModel(
                                        error.errorCode.toString(),
                                        error.message.toString()
                                    )
                                )
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anError?.let {
                            it.printStackTrace()
                            listener.onError(
                                NetworkErrorModel(
                                    anError.errorCode.toString(),
                                    anError.message.toString()
                                )
                            )
                        }
                    }
                })

        }

        fun getReport(summaryDate: String, shift: String, listener: NetworkLisener<GetReportResponseModel>) {
            AndroidNetworking.get(URL_REPORT)
                .addHeaders("Authorization", "Bearer " + PreferenceUtils.token)
                .addHeaders("Content-type", "application/json")
                .addHeaders("Accept", "application/json")
                .addQueryParameter("summary_date", summaryDate)
                .addQueryParameter("shift", shift)
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        response?.let {
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
                                val error = ANError(it.getString("message"))
                                error.errorCode = status
                                listener.onError(
                                    NetworkErrorModel(
                                        error.errorCode.toString(),
                                        error.message.toString()
                                    )
                                )
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anError?.let {
                            it.printStackTrace()
                            listener.onError(
                                NetworkErrorModel(
                                    anError.errorCode.toString(),
                                    anError.message.toString()
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