package com.likethesalad.android.aaper.api.strategy

import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.api.data.LaunchMetadata
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.api.launcher.RequestLauncher
import com.likethesalad.android.aaper.api.statusprovider.PermissionStatusProvider

/**
 * Placeholder strategy for when no type param is provided in an [EnsurePermissions] annotation.
 */
open class NoopRequestStrategy : RequestStrategy<Any>() {
    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        throw UnsupportedOperationException()
    }

    override fun getLaunchMetadata(host: Any): LaunchMetadata? {
        throw UnsupportedOperationException()
    }

    override fun getPermissionStatusProvider(host: Any): PermissionStatusProvider<Any> {
        throw UnsupportedOperationException()
    }

    override fun getRequestLauncher(host: Any): RequestLauncher<Any> {
        throw UnsupportedOperationException()
    }
}