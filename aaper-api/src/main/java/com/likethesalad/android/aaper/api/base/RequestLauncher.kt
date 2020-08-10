package com.likethesalad.android.aaper.api.base

/**
 * Created by César Muñoz on 02/08/20.
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

    abstract fun launchPermissionsRequest(host: T, permissions: List<String>, requestCode: Int)
}