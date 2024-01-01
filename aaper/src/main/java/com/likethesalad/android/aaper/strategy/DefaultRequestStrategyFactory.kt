package com.likethesalad.android.aaper.strategy

import android.content.Context
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory

/**
 * Default factory that instantiates strategies that have either a constructor with a [Context], or an empty constructor.
 * If the strategy has a [Context] param, the Application context will be passed.
 *
 * This factory doesn't work for strategies that need other types of constructor params, for those you should create and set your own factory.
 */
class DefaultRequestStrategyFactory(private val applicationContext: Context) :
    RequestStrategyFactory {
    override fun <T : RequestStrategy<out Any>> getStrategy(host: Any, type: Class<T>): T {
        return try {
            type.getConstructor(Context::class.java).newInstance(applicationContext)
        } catch (e: NoSuchMethodException) {
            type.getConstructor().newInstance()
        }
    }
}