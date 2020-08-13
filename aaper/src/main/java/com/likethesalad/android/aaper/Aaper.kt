package com.likethesalad.android.aaper

import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import com.likethesalad.android.aaper.internal.base.RequestStrategyProviderSource
import com.likethesalad.android.aaper.defaults.strategies.DefaultRequestStrategy
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyProvider

/**
 * Aaper entry point.
 */
object Aaper : RequestStrategyProviderSource {

    private lateinit var strategyProvider: RequestStrategyProvider
    private var initialized = false

    /**
     * Initializes Aaper, this function must be called once only and it must be done
     * before any permission request. Therefore, a great place to call it is the
     * "Application.onCreate" function.
     *
     * @param strategyProvider - (Optional) If you want to override the default [RequestStrategyProvider]
     * instance by a custom one, you can pass yours here. Otherwise, the default will be
     * [DefaultRequestStrategyProvider].
     */
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

    /**
     * Returns the instance of [RequestStrategyProvider] being used, which is the one set on
     * the [init] function. If no custom instance was passed, then this function will return a
     * [DefaultRequestStrategyProvider] one.
     */
    override fun getRequestStrategyProvider(): RequestStrategyProvider {
        return strategyProvider
    }
}