package com.likethesalad.android.aaper.api.base

/**
 * Created by César Muñoz on 02/08/20.
 */
abstract class PermissionStatusProvider<T> {

    abstract fun isPermissionGranted(host: T, permissionName: String): Boolean
}