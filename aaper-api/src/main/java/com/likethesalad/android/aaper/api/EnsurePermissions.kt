package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider

/**
 * This annotation is for the methods inside supported hosts (either an Activity or a Fragment)
 * that will be handled by Aaper in order to request (if needed) their required permissions.
 *
 * @param permissions - The array of permission that the annotated function
 * needs in order to be run.
 *
 * @param strategyName - (Optional) The name of the [RequestStrategy] that will take care of
 * handling the permissions request. A default strategy will be used if not provided.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class EnsurePermissions(
    val permissions: Array<String>,
    val strategyName: String = RequestStrategyProvider.DEFAULT_STRATEGY
)