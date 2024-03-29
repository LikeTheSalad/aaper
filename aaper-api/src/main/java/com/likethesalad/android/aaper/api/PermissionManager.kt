package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.data.LaunchMetadata
import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.api.statusprovider.PermissionStatusProvider
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory
import com.likethesalad.android.aaper.internal.data.CurrentRequest
import com.likethesalad.android.aaper.internal.data.PendingRequest
import com.likethesalad.android.aaper.internal.strategy.RequestStrategyFactoryProvider
import com.likethesalad.android.aaper.internal.utils.RequestRunner

/**
 * This object is the core of the permissions handling system, it's where the
 * functions are passed to and processed by provided [RequestStrategy] instances.
 */
object PermissionManager {

    private var strategyFactoryProvider: RequestStrategyFactoryProvider? = null
    private val strategyFactory by lazy {
        strategyFactoryProvider!!.getRequestStrategyFactory<RequestStrategyFactory>()
    }
    private var currentRequest: CurrentRequest? = null

    /**
     * This setter is to define the source for a [RequestStrategyFactory] instance.
     * It can only be called once.
     *
     * @param provider - The provider of the [RequestStrategyFactory] instance.
     */
    @JvmStatic
    fun setStrategyFactoryProvider(provider: RequestStrategyFactoryProvider) {
        if (strategyFactoryProvider != null) {
            throw IllegalStateException("There's a source already set")
        }
        strategyFactoryProvider = provider
    }

    /**
     * This function is for starting the permission request process for a method
     * (called the original method). It first gets a [RequestStrategy] by its name,
     * then verifies if some permissions are missing, if not,
     * then it calls the original method right away, otherwise it continues with the
     * request process.
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param originalMethod - The method annotated with [EnsurePermissions].
     * @param permissions - The array of permissions that the original method needs.
     * @param strategyType - The strategy that will take care of the process for the
     * originalMethod.
     */
    @JvmStatic
    fun processPermissionRequest(
        host: Any,
        originalMethod: Runnable,
        permissions: Array<String>,
        strategyType: Class<out RequestStrategy<out Any>>
    ) {
        val strategy = strategyFactory.getStrategy(host, strategyType)
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
     * the request and then it cleans up any trace of such request.
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param permissionsRequested - The array of permissions that the original method requires.
     * @param launchMetadata - The request launch metadata used to launch the permission request.
     */
    @JvmStatic
    fun processPermissionResponse(
        host: Any,
        permissionsRequested: Array<out String>,
        launchMetadata: LaunchMetadata?
    ) {
        val request = currentRequest ?: return
        if (host != request.host) {
            return
        }
        if (!areMetadatasEqual(launchMetadata, request.strategy.internalGetLaunchMetadata(host))) {
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

    private fun areMetadatasEqual(
        responseProvided: LaunchMetadata?,
        requestProvided: LaunchMetadata?
    ): Boolean {
        if (requestProvided == null && responseProvided == null) {
            return true
        }

        return requestProvided?.isEqualTo(responseProvided) == true
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
                strategy.internalGetLaunchMetadata(host)
            )
    }

    private fun cleanUp() {
        currentRequest = null
    }

    fun resetForTest() {
        strategyFactoryProvider = null
    }
}