package com.likethesalad.android.aaper.strategies.defaults

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider

/**
 * Created by César Muñoz on 03/08/20.
 */
class AllPermissionStatusProvider : PermissionStatusProvider<Activity>() {

    override fun isPermissionGranted(host: Activity, permissionName: String): Boolean {
        return ContextCompat.checkSelfPermission(
            host,
            permissionName
        ) == PackageManager.PERMISSION_GRANTED
    }
}