package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.internal.base.RequestStrategyProviderSource
import com.likethesalad.android.aaper.internal.data.CurrentRequest
import com.likethesalad.android.aaper.internal.data.PendingRequest
import com.likethesalad.android.aaper.internal.utils.RequestRunner

/**
 * This object is the core of the permissions handling system, it's where the
 * functions are passed to and processed by provided [RequestStrategy] instances.
 */
object PermissionManager {

    private var strategyProviderSource: RequestStrategyProviderSource? = null
    private val strategyProvider by lazy {
        strategyProviderSource!!.getRequestStrategyProvider()
    }
    private var currentRequest: CurrentRequest? = null

    /**
     * This setter is to define the source for a [RequestStrategyProvider] instance.
     * It can only be called once.
     *
     * @param source - The source for the [RequestStrategyProvider] instance.
     */
    fun setStrategyProviderSource(source: RequestStrategyProviderSource) {
        if (strategyProviderSource != null) {
            throw IllegalStateException("There's a source already set")
        }
        strategyProviderSource = source
    }

    /**
     * This function is for starting the permission request process for a method
     * (called the original method). It first gets a [RequestStrategy] by its name,
     * then verifies if some permissions are missing, if not,
     * then it calls the original method right away, otherwise it continues with the
     * request process.
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param permissions - The array of permissions that the original method needs.
     * @param originalMethod - The method annotated with [EnsurePermissions].
     * @param strategyName - The strategy name that will take care of the process for the
     * originalMethod.
     */
    fun processPermissionRequest(
        host: Any,
        permissions: Array<String>,
        originalMethod: Runnable,
        strategyName: String
    ) {
        val strategy = strategyProvider.getStrategy(host, strategyName)
        val missingPermissions = getMissingPermissions(
            host,
            strategy.internalGetPermissionStatusProvider(host),
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

    /**
     * This function processes the response of a previously launched permission
     * request. It mostly delegates the actions to the [RequestStrategy] used to make
     * the request and then it cleans up this instance, if needed.
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param requestCode - The request code used to launch the permission request.
     * @param permissionsRequested - The array of permissions that the original method requires.
     */
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
        host: Any, strategy: RequestStrategy<out Any>, result: PermissionsResult
    ): Boolean {
        return strategy.internalOnPermissionsRequestResults(
            host, result
        )
    }

    private fun getPermissionsResult(
        host: Any,
        strategy: RequestStrategy<out Any>,
        requestData: PermissionsRequest,
        permissionsRequested: Array<out String>
    ): PermissionsResult {
        val permissionStatusProvider = strategy.internalGetPermissionStatusProvider(host)
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()

        permissionsRequested.forEach { permission ->
            when (permissionStatusProvider.internalIsPermissionGranted(host, permission)) {
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
        strategy: RequestStrategy<out Any>
    ) {
        if (currentRequest != null) {
            return
        }

        val data = PermissionsRequest(permissions.toList(), missingPermissions)

        val pendingRequest = PendingRequest(host, data, strategy, originalMethod)
        val requestRunner = RequestRunner(pendingRequest, ::launchPermissionsRequest)

        if (strategy.internalOnBeforeLaunchingRequest(host, data, requestRunner)) {
            return
        }

        requestRunner.run()
    }

    private fun getMissingPermissions(
        host: Any,
        permissionStatusProvider: PermissionStatusProvider<out Any>,
        permissions: Array<String>
    ): List<String> {
        return permissions.filter {
            !permissionStatusProvider.internalIsPermissionGranted(host, it)
        }
    }

    private fun launchPermissionsRequest(pendingRequest: PendingRequest) {
        val host = pendingRequest.host
        val originalMethod = pendingRequest.originalMethod
        val strategy = pendingRequest.strategy
        val data = pendingRequest.data

        currentRequest = CurrentRequest(host, data, strategy, originalMethod)

        strategy.internalGetRequestLauncher(host)
            .internalLaunchPermissionsRequest(
                host,
                data.missingPermissions,
                strategy.getRequestCode()
            )
    }

    private fun cleanUp() {
        currentRequest = null
    }
}