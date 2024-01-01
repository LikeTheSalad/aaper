package com.likethesalad.android.aaper.internal.base

import com.likethesalad.android.aaper.api.strategy.RequestStrategyProvider

interface RequestStrategyProviderSource {

    fun <T : RequestStrategyProvider> getRequestStrategyProvider(): T
}