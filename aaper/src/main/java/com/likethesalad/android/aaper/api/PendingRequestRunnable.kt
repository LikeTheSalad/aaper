package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.data.PendingRequest
import com.likethesalad.android.aaper.internal.PermissionManager

/**
 * Created by César Muñoz on 30/07/20.
 */
class PendingRequestRunnable(
    private val pendingRequest: PendingRequest
) : Runnable {

    override fun run() {
        PermissionManager.launchPermissionsRequest(
            pendingRequest.activity,
            pendingRequest.originalMethod,
            pendingRequest.handler,
            pendingRequest.permissions
        )
    }
}