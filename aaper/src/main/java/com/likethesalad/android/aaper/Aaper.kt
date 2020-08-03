package com.likethesalad.android.aaper

import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.api.base.RequestStrategyProviderHolder
import com.likethesalad.android.aaper.strategies.DefaultRequestStrategy
import com.likethesalad.android.aaper.strategies.DefaultRequestStrategyProvider

/**
 * Created by César Muñoz on 03/08/20.
 */
object Aaper : RequestStrategyProviderHolder {

    private var strategyProvider: RequestStrategyProvider = DefaultRequestStrategyProvider()
    private val defaultStrategy = DefaultRequestStrategy()

    init {
        PermissionManager.setStrategyProviderHolder(this)
        with(strategyProvider as DefaultRequestStrategyProvider) {
            register(defaultStrategy)
            setDefaultStrategyName(DefaultRequestStrategy.NAME)
        }
    }

    override fun getRequestStrategyProvider(): RequestStrategyProvider {
        return strategyProvider
    }
}