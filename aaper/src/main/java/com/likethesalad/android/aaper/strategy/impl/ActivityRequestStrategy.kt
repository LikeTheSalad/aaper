package com.likethesalad.android.aaper.strategy.impl

import android.app.Activity
import com.likethesalad.android.aaper.api.launcher.RequestLauncher
import com.likethesalad.android.aaper.api.statusprovider.PermissionStatusProvider
import com.likethesalad.android.aaper.defaults.launchers.ActivityRequestLauncher
import com.likethesalad.android.aaper.defaults.statusproviders.ActivityPermissionStatusProvider
import com.likethesalad.android.aaper.strategy.RequestWithCodeMetadataStrategy

/**
 * Base class to create a strategy for Activities-only as host, meaning that only the annotated
 * functions on Activities can be handled by this Strategy. If for example, there's an attempt
 * to use this strategy to handle annotated functions on a non-Activity class, e.g. A Fragment, then
 * this will cause the app to crash. In order to create a Strategy that works for both, Activities
 * and Fragments, you must extend from [AllRequestStrategy] instead.
 */
abstract class ActivityRequestStrategy : RequestWithCodeMetadataStrategy<Activity>() {

    override fun getRequestLauncher(host: Activity): RequestLauncher<Activity> {
        return ActivityRequestLauncher()
    }

    override fun getPermissionStatusProvider(host: Activity): PermissionStatusProvider<Activity> {
        return ActivityPermissionStatusProvider()
    }
}