package com.likethesalad.android.aaper.internal.interceptors

import android.app.Activity
import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.internal.PermissionManager
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
        @This activity: Activity,
        @SuperCall originalMethod: Runnable,
        @Origin methodReference: Method
    ) {
        val annotation = methodReference.getAnnotation(EnsurePermissions::class.java)
        PermissionManager.processPermissionRequest(
            activity, annotation.permissions, originalMethod, annotation.requestHandlerName
        )
    }
}