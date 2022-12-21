package com.rm.promotion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rm.promotion.databinding.ActivityReprintBinding
import com.rm.promotion.util.SunmiPrintHelper
import com.rm.qrgenerator.EncryptPromotionCode

class ReprintQRActivity : AppCompatActivity() {
    val print_size = 6
    val error_level = 3
    private lateinit var binding: ActivityReprintBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReprintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.let {
            it.btnPrint.setOnClickListener { view ->
                val stationCode = it.stationCode.text.toString().trim()
                val promotionCode = it.promotionId.text.toString().trim()
                val promotionTotal = it.total.text.toString().trim()
                var promotionId = promotionCode
                when (promotionCode.length) {
                    1 -> promotionId = "00" + promotionCode
                    2 -> promotionId = "0" + promotionCode
                    3 -> promotionId = promotionCode
                }
                val finalPromotionTotal =
                    if (promotionTotal.length == 1) "0$promotionTotal" else promotionTotal
                val qrcode = getQRCode(promotionId, finalPromotionTotal, stationCode)
                val linkQR = qrcode.first
                val qrText = qrcode.second
                SunmiPrintHelper.getInstance().setAlign(1)
                SunmiPrintHelper.getInstance().printTitle("จำนวน $promotionTotal สิทธิ์")
                SunmiPrintHelper.getInstance().printQr(linkQR, print_size, error_level)
                SunmiPrintHelper.getInstance().printTitle(qrText)
                SunmiPrintHelper.getInstance().feedPaper()
            }
        }
    }

    fun getQRCode(promotionId: String, promotionTotal: String, sCode: String, ): Pair<String, String> {
        if (promotionTotal.length > 2) return Pair("","")
        val domain_uat = "https://posoillucktest.pttor.com/Register?data="
        val domain_prod = "https://pttstationluckydraw.pttor.com/Register?data="
        val domain = domain_prod

        var qrCode = ""
        var qrcodeText = ""
        val data = StringBuilder()
        var stationCode = sCode
        when (stationCode.length) {
            1 -> stationCode = "00000"+stationCode
            2 -> stationCode = "0000"+stationCode
            3 -> stationCode = "000"+stationCode
            4 -> stationCode = "00"+stationCode
            5 -> stationCode = "0"+stationCode
            else -> stationCode
        }
        data.append(stationCode)
        data.append(promotionId)
        data.append("0")//station_status
        val random = getPromotionRunning()
        data.append(random)//promotion running random
        data.append(promotionTotal)
        val result = EncryptPromotionCode().getEncryptData(data.toString())
        if (result.resultCode == 0) {
            qrcodeText = result.resultEncryptData.qrcodeText
            qrCode = result.resultEncryptData.qrcode
        }
        return Pair(domain+qrCode, qrcodeText)
    }

    private fun getPromotionRunning(): String {
        val charPool : List<Char> = ('A'..'Z') + ('2'..'9')
        val STRING_LENGTH = 4
        val randomString = (1..STRING_LENGTH)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");
        return randomString
    }
}