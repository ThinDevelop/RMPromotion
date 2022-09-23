package com.rm.promotion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rm.promotion.databinding.ActivityLoginBinding
import com.rm.promotion.model.*
import com.rm.promotion.network.NetworkManager
import com.rm.promotion.util.DialogUtils
import com.rm.promotion.util.FileUtils
import com.rm.promotion.util.PreferenceUtils
import com.rm.promotion.util.TimeUtil
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val msg = intent.getStringExtra("msg")
        msg?.let {
            showDialog(it)
        }
        binding.let {
            it.btnLogin.setOnClickListener {view->
                val username = it.edtUsername.text.toString().trim()
                val password = it.edtPassword.text.toString().trim()
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    login(username, password)
                } else {
                    Toast.makeText(this@LoginActivity, R.string.login_please_field_data, Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.include2.txtBusinessDate.text = getString(R.string.header_business_date, PreferenceUtils.getBusinessDate())
        binding.include2.txtSerialNo.text = getString(R.string.header_serial_no, android.os.Build.SERIAL)
        binding.include2.txtShiftNo.text = getString(R.string.header_shift_no, PreferenceUtils.preferenceKeyCurrentShift)
        binding.vresion.text = "version. "+BuildConfig.VERSION_NAME

    }

    fun showDialog(msg: String) {
        DialogUtils.showConfirmDialog(this@LoginActivity, msg, object : DialogUtils.OnClickButtonListener{
            override fun onClick() {}

            override fun getButtonKey(): String {
                return getString(R.string.dialog_msg_ok)
            }
        })
    }

    fun login(username: String, password: String) {
        val dialog = DialogUtils.getLoadingDialog(this@LoginActivity)
        dialog.show()
        NetworkManager.login(username, password, object : NetworkManager.Companion.NetworkLoginLisener<LoginResponseModel> {
            override fun onResponse(response: LoginResponseModel) {
                Log.d("login", "onResponse")
                dialog.dismiss()
                if (FileUtils.fileIsExists(this@LoginActivity)) {
                    val transactionModel = FileUtils.getFile(this@LoginActivity)
                    if (isNewBusinessDate(transactionModel)) {
                        var maxEndDate = 0L
                        transactionModel.shift_data.filter { it.end_date.isNotEmpty() }.forEach {
                            val endDate = TimeUtil.getEndDate(it.end_date).time
                            if (endDate > maxEndDate) {
                                maxEndDate = endDate
                            }
                        }

                        for (shift in transactionModel.shift_data) {
                            if (shift.end_date.isNotEmpty()) {
                                val endDate = TimeUtil.getEndDate(shift.end_date).time
                                if (endDate == maxEndDate) {
                                    if (shift.close_date) {
                                        val sdf = SimpleDateFormat(
                                            DateFormatConstant.yyyy_M_dd,
                                            Locale.getDefault()
                                        )
                                        val oldBusiness = TimeUtil.getDate(shift.business_date)
                                        val systemDate = TimeUtil.getNowDate()
                                       if (oldBusiness.time >= systemDate.time) {
                                           val c: Calendar = Calendar.getInstance()
                                           c.setTime(oldBusiness)
                                           c.add(Calendar.DATE, 1) // number of days to add
                                           val date = sdf.format(c.getTime())
                                           PreferenceUtils.preferenceKeyBusinessDate = date
                                       } else {
                                           PreferenceUtils.preferenceKeyBusinessDate = sdf.format(systemDate)
                                       }
                                        PreferenceUtils.preferenceKeyCurrentShift = "1"

                                        val objectShift = Shift(business_date = PreferenceUtils.preferenceKeyBusinessDate,
                                        shift = "1",
                                        start_date = TimeUtil.getStartDate(Date()),
                                        end_date = "",
                                        close_date = false
                                        )
                                        FileUtils.saveNewBusiness(this@LoginActivity, objectShift)
                                    } else {
                                        val objectShift = Shift(business_date = PreferenceUtils.preferenceKeyBusinessDate,
                                            shift = (shift.shift.toInt()+1).toString(),
                                            start_date = TimeUtil.getStartDate(Date()),
                                            end_date = "",
                                            close_date = false
                                        )
                                        FileUtils.saveNewBusiness(this@LoginActivity, objectShift)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (response.shift.date == null) {
                        val systemDate = TimeUtil.getNowDate()
                        val sdf = SimpleDateFormat(
                            DateFormatConstant.yyyy_M_dd,
                            Locale.getDefault()
                        )
                        PreferenceUtils.preferenceKeyBusinessDate = sdf.format(systemDate)
                        PreferenceUtils.preferenceKeyCurrentShift = "1"

                        val objectShift = Shift(business_date = sdf.format(systemDate),
                            shift = "1",
                            start_date = TimeUtil.getStartDate(Date()),
                            end_date = "",
                            close_date = false
                        )
                        FileUtils.saveNewBusiness(this@LoginActivity, objectShift)

                    } else {
                        PreferenceUtils.preferenceKeyBusinessDate = response.shift.date
                        PreferenceUtils.preferenceKeyCurrentShift = response.shift.shift
                        val objectShift = Shift(business_date = response.shift.date,
                            shift = response.shift.shift,
                            start_date = TimeUtil.getStartDate(Date()),
                            end_date = "",
                            close_date = false
                        )
                        FileUtils.saveNewBusiness(this@LoginActivity, objectShift)
                    }

//                    val objectShift = Shift(business_date = response.shift.date,
//                        shift = response.shift.shift,
//                        start_date = TimeUtil.getStartDate(Date()),
//                        end_date = "",
//                        close_date = false
//                    )
//                    FileUtils.saveNewBusiness(this@LoginActivity, objectShift)
                }

                val login = Login(
                    login_time = PreferenceUtils.loginTime,
                    business_date = PreferenceUtils.preferenceKeyBusinessDate,
                    shift = PreferenceUtils.preferenceKeyCurrentShift,
                    user_id = PreferenceUtils.currentUserId
                )
                FileUtils.saveLogin(this@LoginActivity, login)
                if (!response.promotion_sync || PreferenceUtils.promotion.isEmpty()) {
                    getMaster()
                } else {
                    toMainActivity()
                }
            }

            override fun onResponseOffline() {
                if (FileUtils.fileIsExists(this@LoginActivity)) {
                    val transactionModel = FileUtils.getFile(this@LoginActivity)
                    if (isNewBusinessDate(transactionModel)) {
                        var maxEndDate = 0L
                        transactionModel.shift_data.filter { it.end_date.isNotEmpty() }.forEach {
                            val endDate = TimeUtil.getEndDate(it.end_date).time
                            if (endDate > maxEndDate) {
                                maxEndDate = endDate
                            }
                        }

                        for (shift in transactionModel.shift_data) {
                            if (shift.end_date.isNotEmpty()) {
                                val endDate = TimeUtil.getEndDate(shift.end_date).time
                                if (endDate == maxEndDate) {
                                    if (shift.close_date) {
                                        val sdf = SimpleDateFormat(
                                            DateFormatConstant.yyyy_M_dd,
                                            Locale.getDefault()
                                        )
                                        val oldBusiness = TimeUtil.getDate(shift.business_date)
                                        val systemDate = TimeUtil.getNowDate()
                                        if (oldBusiness.time >= systemDate.time) {
                                           val c: Calendar = Calendar.getInstance()
                                            c.setTime(oldBusiness)
                                            c.add(Calendar.DATE, 1) // number of days to add
                                            val date = sdf.format(c.getTime())
                                            PreferenceUtils.preferenceKeyBusinessDate = date
                                        } else {
                                            PreferenceUtils.preferenceKeyBusinessDate = sdf.format(systemDate)
                                        }
                                        PreferenceUtils.preferenceKeyCurrentShift = "1"

                                        val objectShift = Shift(business_date = PreferenceUtils.preferenceKeyBusinessDate,
                                            shift = "1",
                                            start_date = TimeUtil.getStartDate(Date()),
                                            end_date = "",
                                            close_date = false
                                        )
                                        FileUtils.saveNewBusiness(this@LoginActivity, objectShift)
                                    } else {
                                        val objectShift = Shift(business_date = PreferenceUtils.preferenceKeyBusinessDate,
                                            shift = (shift.shift.toInt()+1).toString(),
                                            start_date = TimeUtil.getStartDate(Date()),
                                            end_date = "",
                                            close_date = false
                                        )
                                        FileUtils.saveNewBusiness(this@LoginActivity, objectShift)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    val sdf = SimpleDateFormat(
                        DateFormatConstant.yyyy_M_dd,
                        Locale.getDefault()
                    )
                    val now = Date()
                    var businessDate = now
                    if (PreferenceUtils.preferenceKeyBusinessDate.isNotEmpty()) {
                        businessDate = sdf.parse(PreferenceUtils.preferenceKeyBusinessDate)
                    }
                    val systemDate = TimeUtil.getNowDate()
                    if (businessDate.time >= systemDate.time) {
                        val c: Calendar = Calendar.getInstance()
                        c.setTime(businessDate)
                        c.add(Calendar.DATE, 1) // number of days to add
                        val date = sdf.format(c.getTime())
                        PreferenceUtils.preferenceKeyBusinessDate = date
                    } else {
                        PreferenceUtils.preferenceKeyBusinessDate = sdf.format(now)
                    }
                    val objectShift = Shift(business_date = PreferenceUtils.preferenceKeyBusinessDate,
                        shift = PreferenceUtils.preferenceKeyCurrentShift,
                        start_date = TimeUtil.getStartDate(now),
                        end_date = "",
                        close_date = false
                    )
                    FileUtils.saveNewBusiness(this@LoginActivity, objectShift)
                }

                val login = Login(
                    login_time = PreferenceUtils.loginTime,
                    business_date = PreferenceUtils.preferenceKeyBusinessDate,
                    shift = PreferenceUtils.preferenceKeyCurrentShift,
                    user_id = PreferenceUtils.currentUserId
                )
                FileUtils.saveLogin(this@LoginActivity, login)
                dialog.dismiss()
                toMainActivity()
            }

            override fun onError(errorModel: NetworkErrorModel) {
                dialog.dismiss()
                Toast.makeText(this@LoginActivity, R.string.username_password_incorrect, Toast.LENGTH_LONG).show()
            }

            override fun onExpired() {
                Log.d("login", "onExpired")
            }
        })
    }

    fun isNewBusinessDate(transactionModel: TransactionModel): Boolean{
        var status = true
            transactionModel.shift_data.forEach {
                if (it.end_date.isEmpty()) {
                    PreferenceUtils.preferenceKeyBusinessDate = it.business_date
                    PreferenceUtils.preferenceKeyCurrentShift = it.shift
                    status = false
                    return@forEach
                }
            }
        return status
    }

    fun getMaster() {
        val dialog = DialogUtils.getLoadingDialog(this@LoginActivity)
        dialog.show()
        NetworkManager.getDataMaster(object : NetworkManager.Companion.NetworkLisener<RMDataModel> {
            override fun onResponse(response: RMDataModel) {
                dialog.dismiss()
                Log.d("getDataMaster", "onResponse")
                toMainActivity()
            }

            override fun onError(errorModel: NetworkErrorModel) {
                dialog.dismiss()
                toMainActivity()
                Log.d("getDataMaster", "onError : "+ errorModel.msg)
            }

            override fun onExpired() {
                dialog.dismiss()
                toMainActivity()
                Log.d("getDataMaster", "onExpired")
            }
        })
    }

    fun toMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        this@LoginActivity.finish()
    }
}