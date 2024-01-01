package com.likethesalad.android.aaper.defaults.statusproviders

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.likethesalad.android.aaper.api.statusprovider.PermissionStatusProvider

/**
 * This class queries a permission granted status using Activity as host.
 */
class ActivityPermissionStatusProvider : PermissionStatusProvider<Activity>() {

    override fun isPermissionGranted(host: Activity, permissionName: String): Boolean {
        return ContextCompat.checkSelfPermission(
            host,
            permissionName
        ) == PackageManager.PERMISSION_GRANTED
    }
}