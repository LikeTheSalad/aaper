package com.likethesalad.android.aaper.api.base

import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.api.utils.RequestRunner

/**
 * Created by César Muñoz on 29/07/20.
 */
@Suppress("UNCHECKED_CAST")
abstract class RequestStrategy<T> {

    companion object {
        const val DEFAULT_REQUEST_CODE = 1202
    }

    open fun getRequestCode(): Int {
        return DEFAULT_REQUEST_CODE
    }

    open fun onBeforeLaunchingRequest(
        host: T,
        data: PermissionsRequest,
        request: RequestRunner
    ): Boolean {
        return false
    }

    abstract fun onPermissionsRequestResults(
        host: T,
        data: PermissionsResult
    ): Boolean

    abstract fun getName(): String

    abstract fun getRequestLauncher(): RequestLauncher<T>

    abstract fun getPermissionStatusProvider(): PermissionStatusProvider<T>
}