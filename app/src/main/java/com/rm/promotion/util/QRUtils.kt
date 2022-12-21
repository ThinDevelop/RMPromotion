package com.rm.promotion.util

import com.rm.qrgenerator.EncryptPromotionCode

class QRUtils {

    companion object {
        fun getQRCode(promotionId: String, promotionTotal: String): Pair<String, String> {
            if (promotionTotal.length > 2) return Pair("","")
            val domain_uat = "https://posoillucktest.pttor.com/Register?data="
            val domain_prod = "https://pttstationluckydraw.pttor.com/Register?data="
            val domain = domain_prod

            var qrCode = ""
            var qrcodeText = ""
            val data = StringBuilder()
            var stationCode = PreferenceUtils.stationCode
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
}