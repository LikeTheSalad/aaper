package com.likethesalad.android.aaper.api.utils

import com.likethesalad.android.aaper.api.data.PendingRequest
import com.likethesalad.android.aaper.api.errors.RequestExecutedAlreadyException
import com.likethesalad.android.aaper.internal.PermissionManager

/**
 * Created by César Muñoz on 30/07/20.
 */
class RequestRunner(
    private val pendingRequest: PendingRequest
) : Runnable {

    private var executed = false

    override fun run() {
        if (executed) {
            throw RequestExecutedAlreadyException(pendingRequest.permissions)
        }

        executed = true
        PermissionManager.launchPermissionsRequest(pendingRequest)
    }
}