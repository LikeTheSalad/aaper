package com.likethesalad.android.aaper.strategies.base

import android.app.Activity
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.strategies.defaults.launchers.ActivityRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.ActivityPermissionStatusProvider

/**
 * Base class to create a strategy for Activities-only as host, meaning that only the annotated
 * functions on Activities can be handled by this Strategy. If for example, there's an attempt
 * to use this strategy to handle annotated functions on a non-Activity class, e.g. A Fragment, then
 * this will cause the app to crash. In order to create a Strategy that works for both, Activities
 * and Fragments, you must extend from [AllRequestStrategy] instead.
 */
abstract class ActivityRequestStrategy : RequestStrategy<Activity>() {

    override fun getRequestLauncher(host: Activity): RequestLauncher<Activity> {
        return ActivityRequestLauncher()
    }

    override fun getPermissionStatusProvider(host: Activity): PermissionStatusProvider<Activity> {
        return ActivityPermissionStatusProvider()
    }
}