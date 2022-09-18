package com.rm.promotion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.rm.promotion.adapter.ProductAdapter
import com.rm.promotion.app.RmPromotionApplication
import com.rm.promotion.databinding.ActivityMainBinding
import com.rm.promotion.model.*
import com.rm.promotion.network.NetworkManager
import com.rm.promotion.util.DialogUtils
import com.rm.promotion.util.FileUtils
import com.rm.promotion.util.PreferenceUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val products = PreferenceUtils.products
        if (!PreferenceUtils.isLoginSuccess || products.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            this@MainActivity.finish()
            return
        }

        val adapter = ProductAdapter(this, products)

        binding.gridview.adapter = adapter
        binding.gridview.setOnItemClickListener { adapterView, view, i, l ->
            val item = adapter.getItem(i)
            val intent = Intent(this, CalculatorActivity::class.java)
            intent.putExtra("product_id", item.id)
            intent.putExtra("product_code", item.code)
            intent.putExtra("product_name", item.name)

            startActivity(intent)
        }

        binding.includeMain.txtBusinessDate.text = getString(R.string.header_business_date, PreferenceUtils.getBusinessDate())
        binding.includeMain.txtSerialNo.text = getString(R.string.header_serial_no, android.os.Build.SERIAL)
        binding.includeMain.txtShiftNo.text = getString(R.string.header_shift_no, PreferenceUtils.preferenceKeyCurrentShift)

        binding.btnCloseShift.setOnClickListener {
            DialogUtils.showActionDialog(this@MainActivity, getString(R.string.dialog_msg_close_shift)
            , object : DialogUtils.OnClickButtonListener {
                    override fun onClick() {}

                    override fun getButtonKey(): String {
                        return getString(R.string.dialog_msg_close_cancel)
                    }
                }, object : DialogUtils.OnClickButtonListener {
                    override fun onClick() {
                        closeShift()
                    }

                    override fun getButtonKey(): String {
                        return getString(R.string.dialog_action_msg_close_shift)
                    }
                })

        }



        binding.btnCloseDay.setOnClickListener {
            DialogUtils.showActionDialog(this@MainActivity, getString(R.string.dialog_msg_close_shift)
                , object : DialogUtils.OnClickButtonListener {
                    override fun onClick() {}

                    override fun getButtonKey(): String {
                        return getString(R.string.dialog_msg_close_cancel)
                    }
                }, object : DialogUtils.OnClickButtonListener {
                    override fun onClick() {
                        closeDay()
                    }

                    override fun getButtonKey(): String {
                        return getString(R.string.dialog_action_msg_close_day)
                    }
                })

        }

        binding.btnSummary.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            logoutWithMSG(getString(R.string.dialog_msg_logout))
        }
    }

    fun logoutWithMSG(key: String) {
        PreferenceUtils.currentUserId = ""
        PreferenceUtils.loginUserName = ""
        PreferenceUtils.cashierName = ""
        PreferenceUtils.loginPassword = ""
        PreferenceUtils.setLoginFail()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("msg", key)
        startActivity(intent)
        this@MainActivity.finish()
    }

    fun closeDay() {
        val data = FileUtils.getJsonObjectFromFile(context = this)
        val newObj = data.putOpt("business_date", PreferenceUtils.preferenceKeyBusinessDate)
            .putOpt("user_id", PreferenceUtils.currentUserId)
            .putOpt("shift", PreferenceUtils.preferenceKeyCurrentShift)
            .putOpt("close_type", "day")

        NetworkManager.closeShift(newObj, object : NetworkManager.Companion.NetworkLisener<CloseShiftResponseModel> {
            override fun onResponse(response: CloseShiftResponseModel) {
                val date = response.business_date
                val shiftModel = response.shift
                PreferenceUtils.preferenceKeyBusinessDate = date
                PreferenceUtils.preferenceKeyCurrentShift = shiftModel
                FileUtils.deleteJsonObjectFromFile(this@MainActivity)
                logoutWithMSG(getString(R.string.dialog_msg_close_day_complete))
                //success delete file and logout
            }

            override fun onError(errorModel: NetworkErrorModel) {
                PreferenceUtils.preferenceKeyCurrentShift = "1"
                val strDate = PreferenceUtils.preferenceKeyBusinessDate
                val sdf = SimpleDateFormat(DateFormatConstant.yyyy_M_dd, Locale.getDefault())

                val c: Calendar = Calendar.getInstance()
                c.setTime(sdf.parse(strDate))
                c.add(Calendar.DATE, 1) // number of days to add
                val date = sdf.format(c.getTime())
                PreferenceUtils.preferenceKeyBusinessDate = date
                logoutWithMSG(getString(R.string.dialog_msg_close_day_complete))
            //logout shift = 1 and businessDate +1
            }

            override fun onExpired() {
                //logout shift = 1 and businessDate +1
            }
        })
    }

    fun closeShift() {
        val oldShift = PreferenceUtils.preferenceKeyCurrentShift
        val data = FileUtils.getJsonObjectFromFile(context = this)
        val newObj = data.putOpt("business_date", PreferenceUtils.preferenceKeyBusinessDate)
            .putOpt("user_id", PreferenceUtils.currentUserId)
            .putOpt("shift", PreferenceUtils.preferenceKeyCurrentShift)
            .putOpt("close_type", "shift")

        NetworkManager.closeShift(newObj, object : NetworkManager.Companion.NetworkLisener<CloseShiftResponseModel> {
            override fun onResponse(response: CloseShiftResponseModel) {
                val date = response.business_date
                val shift = response.shift
                PreferenceUtils.preferenceKeyBusinessDate = date
                PreferenceUtils.preferenceKeyCurrentShift = shift
                FileUtils.deleteJsonObjectFromFile(this@MainActivity)
                logoutWithMSG(getString(R.string.dialog_msg_close_shift_s_complete, oldShift))
                //success delete file and logout add new shift and businessDate
            }

            override fun onError(errorModel: NetworkErrorModel) {
                val shift = PreferenceUtils.preferenceKeyCurrentShift.toInt() +1
                PreferenceUtils.preferenceKeyCurrentShift = shift.toString()
                logoutWithMSG(getString(R.string.dialog_msg_close_shift_s_complete, oldShift))
                //logout shift+1 and businessDate
            }

            override fun onExpired() {
                //logout shift+1 and businessDate
            }
        })
    }
}