package com.likethesalad.android.aaper

import android.content.Context
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import com.likethesalad.android.aaper.errors.AaperNotInitializedException
import com.likethesalad.android.aaper.internal.strategy.RequestStrategyFactoryProvider
import com.likethesalad.android.aaper.strategy.DefaultRequestStrategyFactory

/**
 * Aaper entry point.
 */
object Aaper : RequestStrategyFactoryProvider {

    private var initialized = false
    private var strategyFactory: RequestStrategyFactory? = null
    private var applicationContext: Context? = null

    /**
     * Initializes Aaper
     * This method is supposed to be called automatically so there's no need for it to be called,
     * unless you wanted to override the default behavior, should it be the case, you'd need to
     * disable its automatic initialization first. More info on the README.
     *
     * @param context - The app context
     */
    @JvmStatic
    fun initialize(context: Context) = synchronized(this) {
        if (initialized) {
            throw AaperInitializedAlreadyException()
        }

        this.applicationContext = context.applicationContext
        this.strategyFactory = DefaultRequestStrategyFactory(context)
        PermissionManager.setStrategyFactoryProvider(this)

        initialized = true
    }

    /**
     * Sets a [RequestStrategyFactory] instance. This can only be done once.
     * If no custom instance was passed, then a [DefaultRequestStrategyFactory] instance will be set lazily.
     */
    @JvmStatic
    fun setRequestStrategyFactory(strategyFactory: RequestStrategyFactory) = synchronized(this) {
        verifyInitialization()
        if (this.strategyFactory != null) {
            throw IllegalStateException("The RequestStrategyFactory instance can only be set once.")
        }
        this.strategyFactory = strategyFactory
    }

    /**
     * Returns the provided [RequestStrategyFactory] instance.
     * If no custom instance was passed, then this function will return a
     * [DefaultRequestStrategyFactory] one.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : RequestStrategyFactory> getRequestStrategyFactory(): T = synchronized(this) {
        verifyInitialization()
        if (strategyFactory == null) {
            setRequestStrategyFactory(DefaultRequestStrategyFactory(applicationContext!!))
        }
        return strategyFactory as T
    }

    private fun verifyInitialization() {
        if (!initialized) {
            throw AaperNotInitializedException()
        }
    }

    fun resetForTest() {
        initialized = false
        applicationContext = null
        strategyFactory = null
    }
}