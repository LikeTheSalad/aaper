package com.likethesalad.android.aaper.sample.test.testutils.flow

import com.likethesalad.android.aaper.internal.utils.RequestRunner

object PermissionFlowState {
    var permissionsGranted = false
    var launchCount = 0
    var responseCount = 0
    var beforeLaunchCount = 0
    var lastPermissions: List<String> = emptyList()
    var deferredRunner: RequestRunner? = null

    fun reset() {
        permissionsGranted = false
        launchCount = 0
        responseCount = 0
        beforeLaunchCount = 0
        lastPermissions = emptyList()
        deferredRunner = null
    }
}
