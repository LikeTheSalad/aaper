package com.likethesalad.android.aaper.api.base

/**
 * Created by César Muñoz on 02/08/20.
 */
interface RequestLauncher<T> {

    fun launchPermissionsRequest(host: T, permissions: Array<String>)
}