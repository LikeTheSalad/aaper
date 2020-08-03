package com.likethesalad.android.aaper.strategies

import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.errors.StrategyNameAlreadyExistsException

/**
 * Created by César Muñoz on 03/08/20.
 */
class DefaultRequestStrategyProvider : RequestStrategyProvider() {

    private val strategies = mutableMapOf<String, RequestStrategy<out Any>>()
    private var defaultStrategyName: String = ""

    fun setDefaultStrategyName(name: String) {
        defaultStrategyName = name
    }

    fun register(strategy: RequestStrategy<out Any>) {
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