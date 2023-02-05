package com.likethesalad.android.aaper

import android.content.Context
import androidx.startup.Initializer
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyProvider
import com.likethesalad.android.aaper.defaults.strategies.DefaultRequestStrategy

class AaperInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val strategyProvider = DefaultRequestStrategyProvider()
        strategyProvider.register(DefaultRequestStrategy())
        strategyProvider.setDefaultStrategyName(DefaultRequestStrategy.NAME)

        Aaper.setUp(context, strategyProvider)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
