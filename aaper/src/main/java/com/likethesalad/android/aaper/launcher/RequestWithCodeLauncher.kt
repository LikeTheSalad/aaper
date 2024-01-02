package com.likethesalad.android.aaper.launcher

import com.likethesalad.android.aaper.api.data.LaunchMetadata
import com.likethesalad.android.aaper.api.launcher.RequestLauncher
import com.likethesalad.android.aaper.data.RequestCodeLaunchMetadata

/**
 * Base [RequestLauncher] for common Android component requests in which a numeric code
 * must be provided as identifier of the request.
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

    /**
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param permissions - The permissions being requested.
     * @param requestCode - The request code needed to launch the request.
     */
    abstract fun launchPermissionsRequest(
        host: T,
        permissions: List<String>,
        requestCode: Int
    )
}