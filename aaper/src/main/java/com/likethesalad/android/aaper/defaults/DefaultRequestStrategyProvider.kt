package com.likethesalad.android.aaper.defaults

import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.defaults.strategies.DefaultRequestStrategy
import com.likethesalad.android.aaper.errors.StrategyNameAlreadyExistsException

/**
 * Default strategy provider, it contains only the [DefaultRequestStrategy] by default
 * but it also allows for registration of custom Strategies, as long as the name of the Strategy
 * being registered is not the same as an already registered Strategy.
 *
 * It returns the [DefaultRequestStrategy] instance as default Strategy, however, the default
 * Strategy can be overridden by another (already registered) one by calling the [setDefaultStrategyName]
 * method and providing the name of the Strategy that will be used as default.
 */
class DefaultRequestStrategyProvider : RequestStrategyProvider() {

    private val strategies = mutableMapOf<String, RequestStrategy<out Any>>()
    private var defaultStrategyName: String = ""

    /**
     * Changes the default Strategy name.
     *
     * @param name - The name of the already registered Strategy that will be used
     * as the default one.
     */
    fun setDefaultStrategyName(name: String) {
        defaultStrategyName = name
    }

    /**
     * Registers new Strategy/ies to make it available for the [EnsurePermissions] annotation.
     *
     * @param strategies - The new Strategy/ies, their name must be different from any other
     * Strategy already registered.
     */
    fun register(vararg strategies: RequestStrategy<out Any>) {
        strategies.forEach {
            registerSingle(it)
        }
    }

    private fun registerSingle(strategy: RequestStrategy<out Any>) {
        val name = strategy.getName()
        if (strategies.containsKey(name)) {
            throw StrategyNameAlreadyExistsException(name)
        }

        strategies[name] = strategy
    }

    override fun getStrategyForName(host: Any, name: String): RequestStrategy<out Any> {
        return strategies.getValue(name)
    }

    override fun getDefaultStrategy(host: Any): RequestStrategy<out Any> {
        return getStrategyForName(host, defaultStrategyName)
    }
}