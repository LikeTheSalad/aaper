package com.likethesalad.android.aaper.internal.base

import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory

interface RequestStrategyProviderSource {

    fun <T : RequestStrategyFactory> getRequestStrategyFactory(): T
}