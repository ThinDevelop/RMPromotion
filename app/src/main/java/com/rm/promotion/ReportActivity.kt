package com.rm.promotion

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rm.promotion.databinding.ActivityReportBinding
import com.rm.promotion.model.GetReportResponseModel
import com.rm.promotion.model.NetworkErrorModel
import com.rm.promotion.network.NetworkManager
import com.rm.promotion.util.PreferenceUtils
import com.rm.promotion.util.RMPrintUtil
import java.util.*


class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding

    var shiftPicker = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.includeReport.txtBusinessDate.text = getString(R.string.header_business_date, PreferenceUtils.getBusinessDate())
        binding.includeReport.txtSerialNo.text = getString(R.string.header_serial_no, android.os.Build.SERIAL)
        binding.includeReport.txtShiftNo.text = getString(R.string.header_shift_no, PreferenceUtils.preferenceKeyCurrentShift)
        binding.btnBack.setOnClickListener{
            onBackPressed()
        }

        binding.datePicker1.maxDate = Calendar.getInstance().timeInMillis

        val pickerVals = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20")
        binding.numberPicker.setMaxValue(pickerVals.size-1)
        binding.numberPicker.setMinValue(0)
        binding.numberPicker.setDisplayedValues(pickerVals)

        binding.numberPicker.setOnValueChangedListener{ numberPicker, i, i1 ->
            shiftPicker = numberPicker.getValue()
        }

        binding.btnPrintSummary.setOnClickListener {

            val date = ""+binding.datePicker1.dayOfMonth +"/"+ (binding.datePicker1.getMonth() + 1)+"/"+binding.datePicker1.getYear()
            val date2 = ""+binding.datePicker1.year +"-"+ (binding.datePicker1.month + 1)+"-"+binding.datePicker1.dayOfMonth
            Log.d("picker value", date2 + pickerVals.get(shiftPicker))
            NetworkManager.getReport(date2, pickerVals.get(shiftPicker), object : NetworkManager.Companion.NetworkLisener<GetReportResponseModel> {
                override fun onResponse(response: GetReportResponseModel) {
                    if (response.promotion.isEmpty()) {
                        Toast.makeText(this@ReportActivity, R.string.promotion_not_found, Toast.LENGTH_LONG).show()
                    } else {
                        RMPrintUtil.printReport(
                            reportModel = response,
                            context = this@ReportActivity
                        )
                    }
                }

                override fun onError(errorModel: NetworkErrorModel) {
                    Toast.makeText(this@ReportActivity, R.string.promotion_not_found, Toast.LENGTH_LONG).show()
                }

                override fun onExpired() {

                }
            })
        }
    }
}