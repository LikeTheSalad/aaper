package com.likethesalad.android.aaper.api.base.bytebuddy.interceptors

import com.likethesalad.android.aaper.api.PermissionManager
import net.bytebuddy.implementation.bind.annotation.AllArguments
import net.bytebuddy.implementation.bind.annotation.SuperCall
import net.bytebuddy.implementation.bind.annotation.This

/**
 * Created by César Muñoz on 29/07/20.
 */
object ReceivesRequestResultInterceptor {

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun intercept(
        @This host: Any,
        @SuperCall originalMethodCall: Runnable,
        @AllArguments arguments: Array<Any>
    ) {
        PermissionManager.processPermissionResponse(
            host,
            arguments[0] as Int,
            arguments[1] as Array<out String>
        )
        originalMethodCall.run()
    }
}