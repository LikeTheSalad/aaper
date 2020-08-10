package com.likethesalad.android.aaper.api.base

import com.likethesalad.android.aaper.api.EnsurePermissions

/**
 * This class should provide instances of [RequestStrategy].
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

    /**
     * This method will be called when a non-default strategy name was provided
     * in [EnsurePermissions].
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param name - The name of the [RequestStrategy] requested.
     *
     * @return - The requested [RequestStrategy].
     */
    abstract fun getStrategyForName(host: Any, name: String): RequestStrategy<out Any>

    /**
     * This method will be called if the default strategy name was provided, or by default if no
     * name was provided in [EnsurePermissions].
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     *
     * @return - The default [RequestStrategy].
     */
    abstract fun getDefaultStrategy(host: Any): RequestStrategy<out Any>
}