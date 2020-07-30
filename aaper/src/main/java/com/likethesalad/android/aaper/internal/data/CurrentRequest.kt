package com.likethesalad.android.aaper.internal.data

import android.app.Activity
import com.likethesalad.android.aaper.api.PermissionRequestHandler

/**
 * Created by César Muñoz on 29/07/20.
 */
data class CurrentRequest(
    val activity: Activity,
    val originalMethod: Runnable,
    val handler: PermissionRequestHandler
)