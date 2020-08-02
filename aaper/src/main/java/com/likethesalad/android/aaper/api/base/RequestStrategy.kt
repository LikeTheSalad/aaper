package com.likethesalad.android.aaper.api.base

import com.likethesalad.android.aaper.api.utils.RequestRunner

/**
 * Created by César Muñoz on 29/07/20.
 */
@Suppress("UNCHECKED_CAST")
abstract class RequestStrategy<T> {

    companion object {
        const val DEFAULT_REQUEST_CODE = 1202
    }

    internal fun internalOnBeforeLaunchingRequest(
        host: Any,
        forPermissions: Array<String>,
        request: RequestRunner
    ): Boolean {
        return onBeforeLaunchingRequest(host as T, forPermissions, request)
    }

    internal fun internalOnPermissionsRequestResults(
        host: Any,
        permissionsGranted: List<String>,
        permissionsDenied: List<String>
    ): Boolean {
        return onPermissionsRequestResults(host as T, permissionsGranted, permissionsDenied)
    }

    internal fun internalGetRequestLauncher(host: Any): RequestLauncher<*> {
        return getRequestLauncher(host as T)
    }

    internal fun internalGetPermissionStatusProvider(host: Any): PermissionStatusProvider {
        return getPermissionStatusProvider(host as T)
    }

    open fun getRequestCode(): Int {
        return DEFAULT_REQUEST_CODE
    }

    open fun onBeforeLaunchingRequest(
        host: T,
        forPermissions: Array<String>,
        request: RequestRunner
    ): Boolean {
        return false
    }

    abstract fun getName(): String

    abstract fun getRequestLauncher(host: T): RequestLauncher<T>

    abstract fun getPermissionStatusProvider(host: T): PermissionStatusProvider

    abstract fun onPermissionsRequestResults(
        host: T,
        permissionsGranted: List<String>,
        permissionsDenied: List<String>
    ): Boolean
}