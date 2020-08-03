package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.base.RequestStrategyProvider

/**
 * Created by César Muñoz on 25/07/20.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class EnsurePermissions(
    val permissions: Array<String>,
    val strategyName: String = RequestStrategyProvider.DEFAULT_STRATEGY
)