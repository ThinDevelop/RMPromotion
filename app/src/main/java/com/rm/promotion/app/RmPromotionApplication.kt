package com.rm.promotion.app

import android.app.Application
import android.content.Context
import com.rm.promotion.util.PreferenceUtils
import com.rm.promotion.util.SunmiPrintHelper
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService

class RmPromotionApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceUtils.init(applicationContext)
        appContext = applicationContext
        init()
    }

    /**
     * Connect print service through interface library
     */
    private fun init() {
        SunmiPrintHelper.getInstance()
            .initSunmiPrinterService(this)
    }

    fun getContext(): Context {
        return appContext
    }

    companion object {
        lateinit var appContext: Context
        var sunmiPrinterService: SunmiPrinterService? = null

        fun setPrintService(service: SunmiPrinterService) {
            sunmiPrinterService = service
        }
    }
}