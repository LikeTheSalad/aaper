package com.likethesalad.android.aaper.strategies.defaults.launchers

import com.likethesalad.android.aaper.api.base.LaunchMetadata
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.launchmetadata.RequestCodeLaunchMetadata

/**
 * Created by César Muñoz on 13/08/20.
 */
abstract class RequestWithCodeLauncher<T> : RequestLauncher<T>() {

    override fun launchPermissionsRequest(
        host: T,
        permissions: List<String>,
        launchMetadata: LaunchMetadata?
    ) {
        val requestCode = (launchMetadata as RequestCodeLaunchMetadata).code
        launchPermissionsRequest(host, permissions, requestCode)
    }

    abstract fun launchPermissionsRequest(
        host: T,
        permissions: List<String>,
        requestCode: Int
    )
}