package com.likethesalad.android.aaper.strategies.defaults.launchers

import android.app.Activity
import androidx.core.app.ActivityCompat
import com.likethesalad.android.aaper.api.base.RequestLauncher

/**
 * This class launches a request permission using an Activity as host.
 */
class ActivityRequestLauncher : RequestLauncher<Activity>() {

    override fun launchPermissionsRequest(
        host: Activity,
        permissions: List<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(host, permissions.toTypedArray(), requestCode)
    }
}