package com.likethesalad.android.aaper.api.base

import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.internal.utils.RequestRunner

/**
 * This is the base class for a request strategy.
 */
abstract class RequestStrategy<T> {

    @Suppress("UNCHECKED_CAST")
    internal fun internalOnBeforeLaunchingRequest(
        host: Any,
        data: PermissionsRequest,
        request: RequestRunner
    ): Boolean {
        return onBeforeLaunchingRequest(host as T, data, request)
    }

    @Suppress("UNCHECKED_CAST")
    internal fun internalOnPermissionsRequestResults(
        host: Any,
        data: PermissionsResult
    ): Boolean {
        return onPermissionsRequestResults(host as T, data)
    }

    @Suppress("UNCHECKED_CAST")
    fun internalGetPermissionStatusProvider(host: Any): PermissionStatusProvider<T> {
        return getPermissionStatusProvider(host as T)
    }

    @Suppress("UNCHECKED_CAST")
    fun internalGetRequestLauncher(host: Any): RequestLauncher<T> {
        return getRequestLauncher(host as T)
    }

    @Suppress("UNCHECKED_CAST")
    fun internalGetLaunchMetadata(host: Any): LaunchMetadata? {
        return getLaunchMetadata(host as T)
    }

    /**
     * This function is called right before launching the permissions request and, depending
     * on the value it returns, it can be used to delay the request launch (for example when
     * it's needed to show a dialog explaining why the permissions are needed).
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param data - The request data.
     * @param request - The request runner, can be used to delegate the request launch.
     *
     * @return - FALSE if no need to delay the request launch and rather run it right away.
     * TRUE if the request will be manually launched later by triggering the request runner.
     */
    open fun onBeforeLaunchingRequest(
        host: T,
        data: PermissionsRequest,
        request: RequestRunner
    ): Boolean {
        return false
    }

    /**
     * This function will be called after receiving the permission request results and,
     * the value it returns will determine whether the request was successful or not and
     * whether the original method can be called or not.
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param data - The result of the request.
     *
     * @return - TRUE, if the result is considered a success and thus the annotated, original
     * method will be called.
     * FALSE, if the result is considered a failure and therefore the annotated, original
     * will not be called.
     */
    abstract fun onPermissionsRequestResults(
        host: T,
        data: PermissionsResult
    ): Boolean

    /**
     * This getter must provide the name of this [RequestStrategy], it's the same name
     * that users can later (optionally) provide in a parameter of the [EnsurePermissions]
     * annotation.
     */
    abstract fun getName(): String

    /**
     * This getter must provide a [RequestLauncher] instance which will be used to
     * launch a request.
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     */
    abstract fun getRequestLauncher(host: T): RequestLauncher<T>

    /**
     * This getter must provide a [PermissionStatusProvider] instance which will
     * be used to query the status of the permissions required for a method.
     *
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     */
    abstract fun getPermissionStatusProvider(host: T): PermissionStatusProvider<T>

    /**
     * This getter is called before launching the request and it must provide
     * the metadata needed, if any, to launch the request. For example, a request code number.
     *
     * @return - The metadata needed, if any, to launch the request.
     */
    abstract fun getLaunchMetadata(host: T): LaunchMetadata?
}