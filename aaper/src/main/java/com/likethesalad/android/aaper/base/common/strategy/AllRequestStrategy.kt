package com.likethesalad.android.aaper.base.common.strategy

import android.app.Activity
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.launcher.RequestLauncher
import com.likethesalad.android.aaper.api.statusprovider.PermissionStatusProvider
import com.likethesalad.android.aaper.base.activity.launcher.ActivityRequestLauncher
import com.likethesalad.android.aaper.base.activity.statusprovider.ActivityPermissionStatusProvider
import com.likethesalad.android.aaper.base.fragment.launcher.FragmentRequestLauncher
import com.likethesalad.android.aaper.base.fragment.statusprovider.FragmentPermissionStatusProvider
import com.likethesalad.android.aaper.strategy.RequestWithCodeMetadataStrategy

/**
 * Base class to create a strategy for both Activities and Fragments as supported hosts,
 * meaning that all of the annotated functions in either an Activity or a Fragment will be
 * handled by this Strategy.
 */
abstract class AllRequestStrategy : RequestWithCodeMetadataStrategy<Any>() {

    @Suppress("UNCHECKED_CAST")
    override fun getRequestLauncher(host: Any): RequestLauncher<Any> {
        return when (host) {
            is Activity -> ActivityRequestLauncher()
            is Fragment -> FragmentRequestLauncher()
            else -> throw UnsupportedOperationException()
        } as RequestLauncher<Any>
    }

    @Suppress("UNCHECKED_CAST")
    override fun getPermissionStatusProvider(host: Any): PermissionStatusProvider<Any> {
        return when (host) {
            is Activity -> ActivityPermissionStatusProvider()
            is Fragment -> FragmentPermissionStatusProvider()
            else -> throw UnsupportedOperationException()
        } as PermissionStatusProvider<Any>
    }
}