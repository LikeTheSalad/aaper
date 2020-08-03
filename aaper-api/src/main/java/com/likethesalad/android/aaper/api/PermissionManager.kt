package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProviderHolder
import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.api.utils.RequestRunner
import com.likethesalad.android.aaper.internal.data.CurrentRequest
import com.likethesalad.android.aaper.internal.data.PendingRequest

/**
 * Created by César Muñoz on 29/07/20.
 */
object PermissionManager {

    private var strategyProviderHolder: RequestStrategyProviderHolder? = null
    private val strategyProvider by lazy {
        strategyProviderHolder!!.getRequestStrategyProvider()
    }
    private var currentRequest: CurrentRequest? = null

    fun setStrategyProviderHolder(holder: RequestStrategyProviderHolder) {
        if (strategyProviderHolder != null) {
            throw IllegalStateException("There's a holder already set")
        }
        strategyProviderHolder = holder
    }

    fun processPermissionRequest(
        host: Any,
        permissions: Array<String>,
        originalMethod: Runnable,
        strategyName: String
    ) {
        val strategy = strategyProvider.getStrategy(host, strategyName)
        val missingPermissions = getMissingPermissions(
            host,
            strategy.getPermissionStatusProvider(),
            permissions
        )

        if (missingPermissions.isEmpty()) {
            originalMethod.run()
            return
        }

        requestPermissions(
            host,
            permissions,
            missingPermissions,
            originalMethod,
            strategy
        )
    }

    fun processPermissionResponse(
        host: Any,
        requestCode: Int,
        permissionsRequested: Array<out String>
    ) {
        val request = currentRequest ?: return
        if (host != request.host) {
            return
        }
        if (requestCode != request.strategy.getRequestCode()) {
            return
        }

        val permissionsResult = getPermissionsResult(
            host,
            request.strategy,
            request.data,
            permissionsRequested
        )

        try {
            if (delegatePermissionResultHandling(host, request.strategy, permissionsResult)) {
                request.originalMethod.run()
            }
        } finally {
            cleanUp()
        }
    }

    private fun delegatePermissionResultHandling(
        host: Any, strategy: RequestStrategy<Any>, result: PermissionsResult
    ): Boolean {
        return strategy.onPermissionsRequestResults(
            host, result
        )
    }

    private fun getPermissionsResult(
        host: Any,
        strategy: RequestStrategy<Any>,
        requestData: PermissionsRequest,
        permissionsRequested: Array<out String>
    ): PermissionsResult {
        val permissionStatusProvider = strategy.getPermissionStatusProvider()
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()

        permissionsRequested.forEach { permission ->
            when (permissionStatusProvider.isPermissionGranted(host, permission)) {
                true -> granted.add(permission)
                false -> denied.add(permission)
            }
        }

        return PermissionsResult(requestData, granted, denied)
    }

    private fun requestPermissions(
        host: Any,
        permissions: Array<String>,
        missingPermissions: List<String>,
        originalMethod: Runnable,
        strategy: RequestStrategy<Any>
    ) {
        if (currentRequest != null) {
            return
        }

        val data = PermissionsRequest(permissions.toList(), missingPermissions)

        val pendingRequest = PendingRequest(host, data, strategy, originalMethod)
        val requestRunner = RequestRunner(pendingRequest, ::launchPermissionsRequest)

        if (strategy.onBeforeLaunchingRequest(host, data, requestRunner)) {
            return
        }

        requestRunner.run()
    }

    private fun getMissingPermissions(
        host: Any,
        permissionStatusProvider: PermissionStatusProvider<Any>,
        permissions: Array<String>
    ): List<String> {
        return permissions.filter {
            !permissionStatusProvider.isPermissionGranted(host, it)
        }
    }

    private fun launchPermissionsRequest(pendingRequest: PendingRequest) {
        val host = pendingRequest.host
        val originalMethod = pendingRequest.originalMethod
        val strategy = pendingRequest.strategy
        val data = pendingRequest.data

        currentRequest = CurrentRequest(host, data, strategy, originalMethod)

        strategy.getRequestLauncher().launchPermissionsRequest(host, data.missingPermissions)
    }

    private fun cleanUp() {
        currentRequest = null
    }
}