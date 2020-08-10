package com.likethesalad.android.aaper.strategies.base

import android.app.Activity
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.ActivityPermissionStatusProvider
import com.likethesalad.android.aaper.strategies.defaults.launchers.ActivityRequestLauncher

/**
 * Created by César Muñoz on 03/08/20.
 */
abstract class ActivityRequestStrategy : RequestStrategy<Activity>() {

    override fun getRequestLauncher(host: Activity): RequestLauncher<Activity> {
        return ActivityRequestLauncher()
    }

    override fun getPermissionStatusProvider(host: Activity): PermissionStatusProvider<Activity> {
        return ActivityPermissionStatusProvider()
    }
}