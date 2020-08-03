package com.likethesalad.android.aaper.api.base

/**
 * Created by César Muñoz on 02/08/20.
 */
abstract class RequestStrategyProvider {

    companion object {
        const val DEFAULT_STRATEGY = "[default.strategy]"
    }

    internal fun getStrategy(host: Any, name: String): RequestStrategy<out Any> {
        return if (name == DEFAULT_STRATEGY) {
            getDefaultStrategy(host)
        } else {
            getStrategyForName(host, name)
        }
    }

    abstract fun getStrategyForName(host: Any, name: String): RequestStrategy<out Any>

    abstract fun getDefaultStrategy(host: Any): RequestStrategy<out Any>
}