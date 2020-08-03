package com.likethesalad.android.aaper.api.data

/**
 * Created by César Muñoz on 03/08/20.
 */
data class PermissionsResult(
    val request: PermissionsRequest,
    val granted: List<String>,
    val denied: List<String>
)