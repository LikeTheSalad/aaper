package com.likethesalad.android.aaper

import android.content.Context
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyProvider
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import com.likethesalad.android.aaper.internal.base.RequestStrategyProviderSource

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
    @Deprecated(message = "Deprecated after adding automatic initialization support on version 2.1.0, there's no longer need to manually call this method.")
    @JvmOverloads
    fun init(strategyProvider: RequestStrategyProvider = DefaultRequestStrategyProvider()) {
        // No operation
    }

    /**
     * Initializes Aaper
     * This method is supposed to be called automatically so there's no need for it to be called,
     * unless you wanted to override the default behavior, should it be the case, you'd need to
     * disable its automatic initialization first. More info on the README.
     *
     * @param context - The app context
     * @param strategyProvider - Will delegate permission requests to its strategies.
     */
    fun setUp(context: Context, strategyProvider: RequestStrategyProvider) = synchronized(this) {
        if (initialized) {
            throw AaperInitializedAlreadyException()
        }

        this.strategyProvider = strategyProvider
        PermissionManager.setStrategyProviderSource(this)

        initialized = true
    }

    /**
     * Returns the instance of [RequestStrategyProvider] being used, which is the one set on
     * the [init] function. If no custom instance was passed, then this function will return a
     * [DefaultRequestStrategyProvider] one.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : RequestStrategyProvider> getRequestStrategyProvider(): T {
        return strategyProvider as T
    }

    fun resetForTest() {
        initialized = false
    }
}