package com.rm.promotion.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.rm.promotion.MainActivity
import com.rm.promotion.R


class DialogUtils {

    companion object {

        fun getLoadingDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.view_loading)
            return dialog
        }

        fun showConfirmDialog(context: Context, msg: String, listener: OnClickButtonListener?) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.view_confirm_dialog)
            val text = dialog.findViewById(R.id.dialog_title) as TextView
            text.text = msg

            val dialogButton: TextView = dialog.findViewById(R.id.btn_dialog_confirm) as TextView
            dialogButton.setOnClickListener {
                listener?.onClick()
                dialog.dismiss()
            }
            dialogButton.text = listener?.getButtonKey()
            dialog.show()
        }

        fun showActionDialog(context: Context, msg: String, cancelListener: OnClickButtonListener, confirmListener: OnClickButtonListener) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.view_action_dialog)
            val text = dialog.findViewById(R.id.dialog_title) as TextView
            text.text = msg
            val dialogCancel: TextView = dialog.findViewById(R.id.btn_dialog_cancel) as TextView
            dialogCancel.setOnClickListener {
                cancelListener.onClick()
                dialog.dismiss()
            }
            val dialogOK: TextView = dialog.findViewById(R.id.btn_dialog_ok) as TextView
            dialogOK.setOnClickListener {
                confirmListener.onClick()
                dialog.dismiss()
            }
            dialogOK.text = confirmListener.getButtonKey()
            dialogCancel.text = cancelListener.getButtonKey()

            dialog.show()
        }
    }

    interface OnClickButtonListener {
        fun onClick()
        fun getButtonKey(): String
    }

    //ตัวอย่าง
//    DialogUtils.showActionDialog(this@LoginActivity, getString(R.string.dialog_msg_close_shift), object : DialogUtils.OnClickButtonListener {
//        override fun onClick() {
//            val username = it.edtUsername.text.toString().trim()
//            val password = it.edtPassword.text.toString().trim()
//            if (username.isNotEmpty() && password.isNotEmpty()) {
//                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//            } else {
//                Toast.makeText(this@LoginActivity, R.string.username_password_incorrect, Toast.LENGTH_LONG).show()
//            }
//        }
//
//        override fun getButtonKey(): String {
//            return "ไม่ปิด"
//        }
//    }, object : DialogUtils.OnClickButtonListener {
//        override fun onClick() {
//            val username = it.edtUsername.text.toString().trim()
//            val password = it.edtPassword.text.toString().trim()
//            if (username.isNotEmpty() && password.isNotEmpty()) {
//                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//            } else {
//                Toast.makeText(this@LoginActivity, R.string.username_password_incorrect, Toast.LENGTH_LONG).show()
//            }
//        }
//
//        override fun getButtonKey(): String {
//            return "ปิดกะ"
//        }
//    })

}