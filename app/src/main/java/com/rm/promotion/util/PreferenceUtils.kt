package com.rm.promotion.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rm.promotion.model.DateFormatConstant
import com.rm.promotion.model.ProductModel
import com.rm.promotion.model.PromotionModel
import com.rm.promotion.model.UserModel
import com.rm.promotion.toDate
import java.text.SimpleDateFormat
import java.util.*

object PreferenceUtils {
    private const val PREFERENCE_KEY_TOKEN = "token"
    private const val PREFERENCE_KEY_LOGIN_USER_NAME = "login_user_name"
    private const val PREFERENCE_KEY_LOGIN_PASSWORD = "login_password"
    private const val PREFERENCE_KEY_LOGIN_SUCCESS = "login_success"
    private const val PREFERENCE_KEY_LOGIN_TIME = "login_time"
    private const val PREFERENCE_KEY_CURRENT_SHIFT = "current_shift"
    private const val PREFERENCE_KEY_BUSINESS_DATE = "business_date"
    private const val PREFERENCE_KEY_STATION_ID = "station_id"
    private const val PREFERENCE_KEY_STATION_CODE = "station_code"
    private const val PREFERENCE_KEY_STATION_NAME = "station_name"
    private const val PREFERENCE_KEY_USERS = "master_users"
    private const val PREFERENCE_KEY_PRODUCT = "master_product"
    private const val PREFERENCE_KEY_PROMOTION = "promotions"
    private const val PREFERENCE_KEY_USER_ID = "user_id"
    private const val PREFERENCE_KEY_NAME = "name"
    private var mAppContext: Context? = null
    fun init(appContext: Context?) {
        mAppContext = appContext
    }

    private val sharedPreferences: SharedPreferences
        get() = mAppContext!!.getSharedPreferences("rm_promotion", Context.MODE_PRIVATE)
    var stationId: String
        get() = sharedPreferences.getString(PREFERENCE_KEY_STATION_ID, "").toString()
        set(userId) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_STATION_ID, userId).apply()
        }
    var stationCode: String
        get() = sharedPreferences.getString(PREFERENCE_KEY_STATION_CODE, "").toString()
        set(userId) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_STATION_CODE, userId).apply()
        }
    var stationName: String?
        get() = sharedPreferences.getString(PREFERENCE_KEY_STATION_NAME, "")
        set(stationName) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_STATION_NAME, stationName).apply()
        }

    //users list json object
    fun setUsers(users: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(PREFERENCE_KEY_USERS, users).apply()
    }

    val users: List<UserModel>
        get() {
            val users = sharedPreferences.getString(PREFERENCE_KEY_USERS, "")
            if (users.isNullOrEmpty()) {
                return mutableListOf()
            }
            return Gson().fromJson(users, object : TypeToken<List<UserModel?>?>() {}.type)
        }

    //products list json object
    fun setProducts(products: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(PREFERENCE_KEY_PRODUCT, products).apply()
    }

    val products: MutableList<ProductModel>
        get() {
            val products = sharedPreferences.getString(PREFERENCE_KEY_PRODUCT, "").toString()
            if (products.isNullOrEmpty()) {
                return mutableListOf()
            }
            return Gson().fromJson(products, object : TypeToken<List<ProductModel?>?>() {}.type)
        }

    fun setLoginSuccess() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(PREFERENCE_KEY_LOGIN_SUCCESS, true).apply()
    }

    fun setLoginFail() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(PREFERENCE_KEY_LOGIN_SUCCESS, false).apply()
    }

    val isLoginSuccess: Boolean
        get() = sharedPreferences.getBoolean(PREFERENCE_KEY_LOGIN_SUCCESS, false)
    var token: String?
        get() = sharedPreferences.getString(PREFERENCE_KEY_TOKEN, "")
        set(token) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_TOKEN, token).apply()
        }
    var loginUserName: String
        get() = sharedPreferences.getString(PREFERENCE_KEY_LOGIN_USER_NAME, "").toString()
        set(userName) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_LOGIN_USER_NAME, userName).apply()
        }
    var loginPassword: String
        get() = sharedPreferences.getString(PREFERENCE_KEY_LOGIN_PASSWORD, "").toString()
        set(password) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_LOGIN_PASSWORD, password).apply()
        }
    var cashierName: String?
        get() = sharedPreferences.getString(PREFERENCE_KEY_NAME, "ไม่พบผู้ใช้งาน")
        set(name) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_NAME, name).apply()
        }
    var currentUserId: String
        get() = sharedPreferences.getString(PREFERENCE_KEY_USER_ID, "").toString()
        set(userId) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_USER_ID, userId).apply()
        }
    var loginTime: String
        get() = sharedPreferences.getString(PREFERENCE_KEY_LOGIN_TIME, "").toString()
        set(time) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_LOGIN_TIME, time).apply()
        }

    fun setPromotion(promotions: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(PREFERENCE_KEY_PROMOTION, promotions).apply()
    }

    val promotion: List<PromotionModel>
        get() {
            val promotion = sharedPreferences.getString(PREFERENCE_KEY_PROMOTION, "")
            if (promotion.isNullOrEmpty()) {
                return mutableListOf()
            }
            return Gson().fromJson(promotion, object : TypeToken<List<PromotionModel?>?>() {}.type)
        }
    var preferenceKeyCurrentShift: String
        get() = sharedPreferences.getString(PREFERENCE_KEY_CURRENT_SHIFT, "").toString()
        set(shift) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_CURRENT_SHIFT, shift).apply()
        }
    var preferenceKeyBusinessDate: String
        get() = sharedPreferences.getString(PREFERENCE_KEY_BUSINESS_DATE, "").toString()
        set(date) {
            val editor = sharedPreferences.edit()
            editor.putString(PREFERENCE_KEY_BUSINESS_DATE, date).apply()
        }

    fun getBusinessDate(): String {
        val businessDate = preferenceKeyBusinessDate
        val date = businessDate.toDate(DateFormatConstant.yyyy_M_dd)
        val simpleDate = SimpleDateFormat(DateFormatConstant.dd_M_yyyy, Locale.getDefault())
        return simpleDate.format(date)
    }
}