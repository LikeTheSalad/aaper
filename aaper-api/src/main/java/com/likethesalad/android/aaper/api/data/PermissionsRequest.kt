package com.likethesalad.android.aaper.api.data

/**
 * Created by César Muñoz on 03/08/20.
 */
data class PermissionsRequest(
    val permissions: List<String>,
    val missingPermissions: List<String>
)