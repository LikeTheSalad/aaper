package com.likethesalad.android.aaper.sample.test.testutils.launcher

import android.app.Activity
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.base.LaunchMetadata
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.data.RequestCodeLaunchMetadata

class TestRequestLauncher : RequestLauncher<Any>() {

    override fun launchPermissionsRequest(
        host: Any,
        permissions: List<String>,
        launchMetadata: LaunchMetadata?
    ) {
        val codeMetadata = launchMetadata as RequestCodeLaunchMetadata
        when (host) {
            is Activity -> host.onRequestPermissionsResult(
                codeMetadata.code,
                emptyArray(),
                IntArray(0)
            )
            is Fragment -> host.onRequestPermissionsResult(
                codeMetadata.code,
                emptyArray(),
                IntArray(0)
            )
            else -> throw UnsupportedOperationException()
        }
    }
}