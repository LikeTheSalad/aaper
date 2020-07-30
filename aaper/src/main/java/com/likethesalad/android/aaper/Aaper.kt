package com.likethesalad.android.aaper

import com.likethesalad.android.aaper.api.RequestHandlersProvider

/**
 * Created by César Muñoz on 29/07/20.
 */
object Aaper {

    private val provider = RequestHandlersProvider()

    fun getHandlersProvider(): RequestHandlersProvider = provider
}