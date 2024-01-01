package com.likethesalad.android.aaper.internal.strategy

import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory

interface RequestStrategyFactoryProvider {

    fun <T : RequestStrategyFactory> getRequestStrategyFactory(): T
}