package com.likethesalad.android.aaper.sample.custom

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AlertDialog
import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.base.common.strategy.AllRequestStrategy
import com.likethesalad.android.aaper.internal.utils.RequestRunner

/**
 * A custom request strategy to demonstrate the use-case of showing alert dialog
 * to user before requesting permissions.
 */
class AlertDialogStrategy : AllRequestStrategy() {

    override fun onBeforeLaunchingRequest(
        host: Any,
        data: PermissionsRequest,
        request: RequestRunner
    ): Boolean {
        if (host is Context) {
            // A spannable string to set custom styles on string
            val spanBuilder = SpannableStringBuilder().apply {
                append("We need following permissions to run the app efficiently.\n\n")
                data.missingPermissions.forEachIndexed { index, permission ->
                    append("${index + 1}. $permission\n")
                }
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    58,
                    this.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(RelativeSizeSpan(0.9f), 58, this.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }

            AlertDialog.Builder(host)
                .setTitle("Need Permissions")
                .setMessage(spanBuilder)
                .setPositiveButton("Grant") { _, _ -> request.run() }
                .setNegativeButton("Cancel", null)
                .show()
            return true
        }
        return super.onBeforeLaunchingRequest(host, data, request)
    }

    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        // Same way as the DefaultRequestStrategy handles it
        return data.denied.isEmpty()
    }
}