package com.likethesalad.android.aaper.internal.base

import com.likethesalad.android.aaper.api.base.RequestStrategyProvider

interface RequestStrategyProviderSource {

    fun getRequestStrategyProvider(): RequestStrategyProvider
}