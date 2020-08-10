package com.likethesalad.android.aaper.strategies.defaults.launchers

import android.app.Activity
import androidx.core.app.ActivityCompat
import com.likethesalad.android.aaper.api.base.RequestLauncher

/**
 * Created by César Muñoz on 03/08/20.
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