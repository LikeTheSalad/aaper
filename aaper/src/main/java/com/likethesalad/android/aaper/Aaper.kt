package com.likethesalad.android.aaper

import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import com.likethesalad.android.aaper.internal.base.RequestStrategyProviderSource
import com.likethesalad.android.aaper.strategies.DefaultRequestStrategy
import com.likethesalad.android.aaper.strategies.DefaultRequestStrategyProvider

/**
 * Created by César Muñoz on 03/08/20.
 */
object Aaper : RequestStrategyProviderSource {

    private lateinit var strategyProvider: RequestStrategyProvider
    private var initialized = false

    @JvmOverloads
    fun init(strategyProvider: RequestStrategyProvider = DefaultRequestStrategyProvider()) {
        if (initialized) {
            throw AaperInitializedAlreadyException()
        }

        initialized = true

        this.strategyProvider = strategyProvider
        PermissionManager.setStrategyProviderSource(this)

        if (strategyProvider is DefaultRequestStrategyProvider) {
            strategyProvider.register(DefaultRequestStrategy())
            strategyProvider.setDefaultStrategyName(DefaultRequestStrategy.NAME)
        }
    }

    override fun getRequestStrategyProvider(): RequestStrategyProvider {
        return strategyProvider
    }
}