package com.likethesalad.android.aaper.internal.data

import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.data.PermissionsRequest

/**
 * Created by César Muñoz on 29/07/20.
 */
data class CurrentRequest(
    val host: Any,
    val data: PermissionsRequest,
    val strategy: RequestStrategy<out Any>,
    internal val originalMethod: Runnable
)