package com.dhl.loginmvvm.app

import android.app.Application
import blockcanary.BlockCanary
import blockcanary.BlockCanaryConfig



class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        val blockCanaryConfig = BlockCanaryConfig.newBuilder().build()
        BlockCanary.install(this,blockCanaryConfig)
    }
}