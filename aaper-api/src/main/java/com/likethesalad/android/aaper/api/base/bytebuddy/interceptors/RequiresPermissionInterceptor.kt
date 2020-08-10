package com.likethesalad.android.aaper.api.base.bytebuddy.interceptors

import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.api.PermissionManager
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.SuperCall
import net.bytebuddy.implementation.bind.annotation.This
import java.lang.reflect.Method

/**
 * Created by César Muñoz on 25/07/20.
 */
object RequiresPermissionInterceptor {

    @JvmStatic
    fun intercept(
        @This host: Any,
        @SuperCall originalMethod: Runnable,
        @Origin methodReference: Method
    ) {
        val annotation = methodReference.getAnnotation(EnsurePermissions::class.java)
        PermissionManager.processPermissionRequest(
            host, annotation.permissions, originalMethod, annotation.strategyName
        )
    }
}