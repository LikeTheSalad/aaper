package com.likethesalad.android.aaper.api.base

/**
 * This class should launch a permission request.
 */
abstract class RequestLauncher<T> {

    @Suppress("UNCHECKED_CAST")
    internal fun internalLaunchPermissionsRequest(
        host: Any,
        permissions: List<String>,
        requestCode: Int
    ) {
        launchPermissionsRequest(host as T, permissions, requestCode)
    }

    /**
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param permissions - The permissions being requested.
     * @param requestCode - The request code.
     */
    abstract fun launchPermissionsRequest(host: T, permissions: List<String>, requestCode: Int)
}