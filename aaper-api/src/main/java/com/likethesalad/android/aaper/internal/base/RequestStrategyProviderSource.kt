package com.likethesalad.android.aaper.internal.base

import com.likethesalad.android.aaper.api.base.RequestStrategyProvider

/**
 * Created by César Muñoz on 03/08/20.
 */
interface RequestStrategyProviderSource {

    fun getRequestStrategyProvider(): RequestStrategyProvider
}