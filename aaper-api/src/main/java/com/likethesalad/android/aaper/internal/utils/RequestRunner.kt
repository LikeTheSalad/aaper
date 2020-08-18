package com.likethesalad.android.aaper.internal.utils

import com.likethesalad.android.aaper.api.errors.RequestExecutedAlreadyException
import com.likethesalad.android.aaper.internal.data.PendingRequest

class RequestRunner(
    private val pendingRequest: PendingRequest,
    private val f: (PendingRequest) -> Unit
) : Runnable {

    private var executed = false

    override fun run() {
        if (executed) {
            throw RequestExecutedAlreadyException(pendingRequest.data.permissions)
        }

        executed = true
        f.invoke(pendingRequest)
    }
}