package com.likethesalad.android.aaper.api.data

/**
 * This data class holds the result of a request as well as the request data.
 *
 * @param request - The request data.
 * @param granted - The list of permissions granted, if any.
 * @param denied - The list of the permissions denied, if any.
 */
data class PermissionsResult(
    val request: PermissionsRequest,
    val granted: List<String>,
    val denied: List<String>
)