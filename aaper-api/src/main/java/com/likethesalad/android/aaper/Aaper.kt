package com.likethesalad.android.aaper

import com.likethesalad.android.aaper.api.base.RequestStrategyProvider

/**
 * Created by César Muñoz on 29/07/20.
 */
object Aaper {

    private lateinit var provider: RequestStrategyProvider

    fun getStrategyProvider(): RequestStrategyProvider = provider

    fun setStrategyProvider(provider: RequestStrategyProvider) {
        this.provider = provider
    }
}