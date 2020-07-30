package com.likethesalad.android.aaper.api

import android.app.Activity

/**
 * Created by César Muñoz on 29/07/20.
 */
abstract class PermissionRequestHandler {

    companion object {
        const val DEFAULT_REQUEST_CODE = 1000
    }

    open fun getRequestCode(): Int {
        return DEFAULT_REQUEST_CODE
    }

    open fun onShowRequestPermissionRationale(
        activity: Activity,
        showForPermissions: List<String>,
        pendingRequest: PendingRequestRunnable
    ): Boolean {
        return false
    }

    abstract fun getName(): String

    abstract fun onPermissionsRequestResults(
        activity: Activity,
        permissionsGranted: List<String>,
        permissionsDenied: List<String>
    ): Boolean
}