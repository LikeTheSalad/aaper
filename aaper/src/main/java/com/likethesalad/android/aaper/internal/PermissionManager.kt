package com.likethesalad.android.aaper.internal

import android.app.Activity
import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.api.PendingRequestRunnable
import com.likethesalad.android.aaper.api.PermissionRequestHandler
import com.likethesalad.android.aaper.api.data.PendingRequest
import com.likethesalad.android.aaper.internal.data.CurrentRequest
import com.likethesalad.android.aaper.internal.data.PermissionStatuses
import com.likethesalad.android.aaper.utils.AndroidApiHelper
import com.likethesalad.android.aaper.utils.AndroidApiHelper.isPermissionGranted

/**
 * Created by César Muñoz on 29/07/20.
 */
object PermissionManager {

    private var currentRequest: CurrentRequest? = null

    internal fun processPermissionRequest(
        activity: Activity,
        originalMethod: Runnable,
        permissions: Array<String>,
        handlerName: String
    ) {
        val missingPermissions = getMissingPermissions(
            activity, permissions
        )
        if (missingPermissions.isEmpty()) {
            originalMethod.run()
            return
        }

        requestPermissions(
            activity,
            originalMethod,
            missingPermissions,
            handlerName
        )
    }

    internal fun processPermissionResponse(
        activity: Activity, requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val request = currentRequest ?: return
        if (activity != request.activity) {
            return
        }
        if (requestCode != request.handler.getRequestCode()) {
            return
        }

        val statuses = getPermissionStatusesOnResponse(permissions, grantResults)

        try {
            if (delegatePermissionResultHandling(activity, request.handler, statuses)) {
                request.originalMethod.run()
            }
        } finally {
            cleanUp()
        }
    }

    private fun delegatePermissionResultHandling(
        activity: Activity, handler: PermissionRequestHandler, statuses: PermissionStatuses
    ): Boolean {
        return handler.onPermissionsRequestResults(
            activity,
            statuses.permissionsGranted,
            statuses.permissionsDenied
        )
    }

    private fun getPermissionStatusesOnResponse(
        permissions: Array<out String>, grantResults: IntArray
    ): PermissionStatuses {
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()

        permissions.forEachIndexed { index, permission ->
            when (isPermissionGranted(grantResults[index])) {
                true -> granted.add(permission)
                false -> denied.add(permission)
            }
        }

        return PermissionStatuses(granted, denied)
    }

    private fun cleanUp() {
        currentRequest = null
    }

    private fun getMissingPermissions(
        activity: Activity, permissionsRequested: Array<String>
    ): Array<String> {
        return permissionsRequested.filter {
            !isPermissionGranted(activity, it)
        }.toTypedArray()
    }

    private fun requestPermissions(
        activity: Activity,
        originalMethod: Runnable,
        permissions: Array<String>,
        handlerName: String
    ) {
        if (currentRequest != null) {
            return
        }

        val handler = Aaper.getHandlersProvider().getHandlerByName(handlerName)

        val showRequestRationaleFor = getShowRequestRationaleForPermissions(activity, permissions)
        if (showRequestRationaleFor.isNotEmpty()) {
            val pendingRequest = PendingRequest(activity, handler, originalMethod, permissions)
            val wasHandled = handler.onShowRequestPermissionRationale(
                activity, showRequestRationaleFor, PendingRequestRunnable(pendingRequest)
            )
            if (wasHandled) {
                return
            }
        }

        launchPermissionsRequest(activity, originalMethod, handler, permissions)
    }

    internal fun launchPermissionsRequest(
        activity: Activity,
        originalMethod: Runnable,
        handler: PermissionRequestHandler,
        permissions: Array<String>
    ) {
        currentRequest = CurrentRequest(activity, originalMethod, handler)

        AndroidApiHelper.requestPermissions(activity, permissions, handler.getRequestCode())
    }

    private fun getShowRequestRationaleForPermissions(
        activity: Activity,
        permissions: Array<String>
    ): List<String> {
        return AndroidApiHelper.getShowRequestPermissionRationale(activity, permissions)
    }
}