package com.likethesalad.android.aaper.sample.test.testutils

import android.app.Application
import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyProvider
import com.likethesalad.android.aaper.sample.test.testutils.strategies.AlwaysSuccessfulStrategy

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val strategyProvider = DefaultRequestStrategyProvider()
        Aaper.setUp(this, strategyProvider)

        strategyProvider.register(AlwaysSuccessfulStrategy())
        strategyProvider.setDefaultStrategyName(AlwaysSuccessfulStrategy.NAME)
    }
}