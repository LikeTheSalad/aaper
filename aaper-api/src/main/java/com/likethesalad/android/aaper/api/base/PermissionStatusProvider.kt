package com.likethesalad.android.aaper.api.base

/**
 * Created by César Muñoz on 02/08/20.
 */
abstract class PermissionStatusProvider<T> {

    internal fun internalIsPermissionGranted(host: Any, permissionName: String): Boolean {
        return isPermissionGranted(host as T, permissionName)
    }

    abstract fun isPermissionGranted(host: T, permissionName: String): Boolean
}