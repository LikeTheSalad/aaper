package com.likethesalad.android.aaper.api.data

/**
 * This data class holds the request parameters.
 */
data class PermissionsRequest(
    val permissions: List<String>,
    val missingPermissions: List<String>
)