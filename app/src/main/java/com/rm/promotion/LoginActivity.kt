package com.rm.promotion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rm.promotion.databinding.ActivityLoginBinding
import com.rm.promotion.model.Login
import com.rm.promotion.model.LoginResponseModel
import com.rm.promotion.model.NetworkErrorModel
import com.rm.promotion.model.RMDataModel
import com.rm.promotion.network.NetworkManager
import com.rm.promotion.util.DialogUtils
import com.rm.promotion.util.FileUtils
import com.rm.promotion.util.PreferenceUtils

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
                val login = Login(
                    login_time = PreferenceUtils.loginTime,
                    business_date = PreferenceUtils.preferenceKeyBusinessDate,
                    shift = response.shift.shift,
                    user_id = PreferenceUtils.currentUserId
                )
                FileUtils.saveLogin(this@LoginActivity, login)
                if (!response.promotions_sync || PreferenceUtils.promotion.isEmpty()) {
                    getMaster()
                } else {
                    toMainActivity()
                }
            }

            override fun onResponseOffline() {
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