package com.likethesalad.android.aaper.api.data

import com.likethesalad.android.aaper.api.EnsurePermissions

/**
 * This data class holds the request parameters.
 *
 * @param permissions - The permissions provided in the [EnsurePermissions] annotation.
 * @param missingPermissions - The missing permissions taken from the [permissions] provided.
 */
data class PermissionsRequest(
    val permissions: List<String>,
    val missingPermissions: List<String>
)