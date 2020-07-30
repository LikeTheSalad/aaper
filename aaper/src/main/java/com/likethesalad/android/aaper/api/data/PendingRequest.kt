package com.likethesalad.android.aaper.api.data

import android.app.Activity
import com.likethesalad.android.aaper.api.PermissionRequestHandler

/**
 * Created by César Muñoz on 30/07/20.
 */
class PendingRequest(
    val activity: Activity,
    val handler: PermissionRequestHandler,
    val originalMethod: Runnable,
    val permissions: Array<String>
)