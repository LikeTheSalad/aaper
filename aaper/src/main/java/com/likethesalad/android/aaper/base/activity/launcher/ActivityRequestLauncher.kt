package com.likethesalad.android.aaper.base.activity.launcher

import android.app.Activity
import androidx.core.app.ActivityCompat
import com.likethesalad.android.aaper.launcher.RequestWithCodeLauncher

/**
 * This class launches a request permission using an Activity as host.
 */
class ActivityRequestLauncher : RequestWithCodeLauncher<Activity>() {

    override fun launchPermissionsRequest(
        host: Activity,
        permissions: List<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(host, permissions.toTypedArray(), requestCode)
    }
}