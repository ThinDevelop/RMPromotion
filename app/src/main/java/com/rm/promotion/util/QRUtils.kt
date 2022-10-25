package com.rm.promotion.util

import com.rm.qrgenerator.EncryptPromotionCode

class QRUtils {

    companion object {
        fun getQRCode(promotionId: String, promotionTotal: String): Pair<String, String> {
            if (promotionTotal.length > 2) return Pair("","")
            val domain_uat = "https://posoillucktest.pttor.com/Register?data="
            val domain_prod = "https://pttstationluckydraw.pttor.com/Register?data="
            val domain = domain_uat

            var qrCode = ""
            var qrcodeText = ""
            val data = StringBuilder()
            var stationId = PreferenceUtils.stationId
            when (stationId.length) {
                1 -> stationId = "00000"+stationId
                2 -> stationId = "0000"+stationId
                3 -> stationId = "000"+stationId
                4 -> stationId = "00"+stationId
                5 -> stationId = "0"+stationId
                else -> stationId
            }
            data.append(stationId)
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