package com.likethesalad.android.aaper.internal

import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.strategy.RequestStrategy

object PermissionRequestHandler {

    @JvmStatic
    fun processPermissionRequest(
        host: Any,
        originalMethod: Runnable,
        permissions: Array<String>,
        strategyType: Class<out RequestStrategy<out Any>>?
    ) {
        PermissionManager.processPermissionRequest(
            host,
            originalMethod,
            permissions,
            strategyType ?: Aaper.getDefaultStrategyType()
        )
    }
}