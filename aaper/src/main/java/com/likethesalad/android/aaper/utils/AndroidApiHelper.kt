package com.likethesalad.android.aaper.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by César Muñoz on 29/07/20.
 */
object AndroidApiHelper {


    fun isPermissionGranted(activity: Activity, permissionName: String): Boolean {
        val status = ContextCompat.checkSelfPermission(activity, permissionName)
        return isPermissionGranted(status)
    }

    fun isPermissionGranted(permissionStatus: Int): Boolean {
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun getShowRequestPermissionRationale(
        activity: Activity,
        permissions: Array<String>
    ): List<String> {
        return permissions.filter {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
    }
}