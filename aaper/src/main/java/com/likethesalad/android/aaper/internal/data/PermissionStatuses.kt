package com.likethesalad.android.aaper.internal.data

/**
 * Created by César Muñoz on 30/07/20.
 */
data class PermissionStatuses(
    val permissionsGranted: List<String>,
    val permissionsDenied: List<String>
)