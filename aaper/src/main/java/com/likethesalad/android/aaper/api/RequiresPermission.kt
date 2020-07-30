package com.likethesalad.android.aaper.api

/**
 * Created by César Muñoz on 25/07/20.
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RequiresPermission(
    val permissions: Array<String>,
    val requestHandlerName: String = ""
)