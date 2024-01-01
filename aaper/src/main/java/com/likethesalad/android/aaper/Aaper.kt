package com.likethesalad.android.aaper

import android.content.Context
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import com.likethesalad.android.aaper.internal.strategy.RequestStrategyProviderSource
import com.likethesalad.android.aaper.strategy.DefaultRequestStrategyFactory

/**
 * Aaper entry point.
 */
object Aaper : RequestStrategyProviderSource {

    private lateinit var strategyFactory: RequestStrategyFactory
    private var initialized = false

    /**
     * Initializes Aaper
     * This method is supposed to be called automatically so there's no need for it to be called,
     * unless you wanted to override the default behavior, should it be the case, you'd need to
     * disable its automatic initialization first. More info on the README.
     *
     * @param context - The app context
     * @param strategyFactory - Will delegate permission requests to its strategies.
     */
    fun initialize(context: Context) = synchronized(this) {
        if (initialized) {
            throw AaperInitializedAlreadyException()
        }

        this.strategyFactory = DefaultRequestStrategyFactory(context)
        PermissionManager.setStrategyProviderSource(this)

        initialized = true
    }

    /**
     * Returns the instance of [RequestStrategyFactory] being used, which is the one set on
     * the [initialize] function. If no custom instance was passed, then this function will return a
     * [DefaultRequestStrategyFactory] one.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : RequestStrategyFactory> getRequestStrategyFactory(): T {
        return strategyFactory as T
    }

    fun resetForTest() {
        initialized = false
    }
}