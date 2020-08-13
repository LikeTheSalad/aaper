package com.likethesalad.android.aaper.api.base

/**
 * This class should launch a permission request.
 */
abstract class RequestLauncher<T> {

    @Suppress("UNCHECKED_CAST")
    internal fun internalLaunchPermissionsRequest(
        host: Any,
        permissions: List<String>,
        launchMetadata: LaunchMetadata?
    ) {
        launchPermissionsRequest(host as T, permissions, launchMetadata)
    }

    /**
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param permissions - The permissions being requested.
     * @param launchMetadata - The metadata needed, if any, to launch the request.
     */
    abstract fun launchPermissionsRequest(
        host: T,
        permissions: List<String>,
        launchMetadata: LaunchMetadata?
    )
}