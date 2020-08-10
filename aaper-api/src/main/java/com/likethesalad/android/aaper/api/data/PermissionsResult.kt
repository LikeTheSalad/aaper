package com.likethesalad.android.aaper.api.data

/**
 * This data class holds the result of a request as well as the request data.
 */
data class PermissionsResult(
    val request: PermissionsRequest,
    val granted: List<String>,
    val denied: List<String>
)