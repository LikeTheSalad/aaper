package com.likethesalad.android.aaper.sample.test.testutils.strategies

import android.app.Activity
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.data.LaunchMetadata
import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.api.launcher.RequestLauncher
import com.likethesalad.android.aaper.api.statusprovider.PermissionStatusProvider
import com.likethesalad.android.aaper.base.common.strategy.AllRequestStrategy
import com.likethesalad.android.aaper.data.RequestCodeLaunchMetadata
import com.likethesalad.android.aaper.internal.utils.RequestRunner
import com.likethesalad.android.aaper.sample.test.testutils.flow.PermissionFlowState

private class FlowPermissionStatusProvider : PermissionStatusProvider<Any>() {
    override fun isPermissionGranted(host: Any, permissionName: String): Boolean {
        return PermissionFlowState.permissionsGranted
    }
}

private abstract class FlowRequestLauncher : RequestLauncher<Any>() {
    override fun launchPermissionsRequest(
        host: Any,
        permissions: List<String>,
        launchMetadata: LaunchMetadata?
    ) {
        PermissionFlowState.launchCount++
        PermissionFlowState.lastPermissions = permissions
        onLaunch(host, permissions, launchMetadata as RequestCodeLaunchMetadata)
    }

    protected abstract fun onLaunch(
        host: Any,
        permissions: List<String>,
        launchMetadata: RequestCodeLaunchMetadata
    )

    protected fun dispatchPermissionResult(
        host: Any,
        permissions: List<String>,
        launchMetadata: RequestCodeLaunchMetadata
    ) {
        when (host) {
            is Activity -> host.onRequestPermissionsResult(
                launchMetadata.code,
                permissions.toTypedArray(),
                IntArray(permissions.size)
            )
            is Fragment -> host.onRequestPermissionsResult(
                launchMetadata.code,
                permissions.toTypedArray(),
                IntArray(permissions.size)
            )
            else -> throw UnsupportedOperationException()
        }
    }
}

class ImmediateDenyingStrategy : AllRequestStrategy() {
    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        PermissionFlowState.responseCount++
        return false
    }

    override fun getRequestLauncher(host: Any): RequestLauncher<Any> {
        return object : FlowRequestLauncher() {
            override fun onLaunch(
                host: Any,
                permissions: List<String>,
                launchMetadata: RequestCodeLaunchMetadata
            ) {
                dispatchPermissionResult(host, permissions, launchMetadata)
            }
        }
    }

    override fun getPermissionStatusProvider(host: Any): PermissionStatusProvider<Any> {
        return FlowPermissionStatusProvider()
    }
}

class DeferredGrantingStrategy : AllRequestStrategy() {
    override fun onBeforeLaunchingRequest(
        host: Any,
        data: PermissionsRequest,
        request: RequestRunner
    ): Boolean {
        PermissionFlowState.beforeLaunchCount++
        PermissionFlowState.deferredRunner = request
        return true
    }

    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        PermissionFlowState.responseCount++
        return data.denied.isEmpty()
    }

    override fun getRequestLauncher(host: Any): RequestLauncher<Any> {
        return object : FlowRequestLauncher() {
            override fun onLaunch(
                host: Any,
                permissions: List<String>,
                launchMetadata: RequestCodeLaunchMetadata
            ) {
                PermissionFlowState.permissionsGranted = true
                dispatchPermissionResult(host, permissions, launchMetadata)
            }
        }
    }

    override fun getPermissionStatusProvider(host: Any): PermissionStatusProvider<Any> {
        return FlowPermissionStatusProvider()
    }
}
