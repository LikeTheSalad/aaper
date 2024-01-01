package com.likethesalad.android.aaper.api.strategy

/**
 * Provides instances of [RequestStrategy].
 */
interface RequestStrategyFactory {

    fun <T : RequestStrategy<out Any>> getStrategy(host: Any, type: Class<T>): T
}