package com.likethesalad.android.aaper.api.base

/**
 * Created by César Muñoz on 02/08/20.
 */
interface RequestStrategyProvider {

    fun getStrategyForName(host: Any, name: String): RequestStrategy<*>

    fun getDefaultStrategy(host: Any): RequestStrategy<*>
}