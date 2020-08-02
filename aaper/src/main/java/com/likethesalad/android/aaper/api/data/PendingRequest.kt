package com.likethesalad.android.aaper.api.data

import android.app.Activity
import com.likethesalad.android.aaper.api.base.RequestStrategy

/**
 * Created by César Muñoz on 30/07/20.
 */
class PendingRequest(
    val activity: Activity,
    val strategy: RequestStrategy,
    val originalMethod: Runnable,
    val permissions: Array<String>
)