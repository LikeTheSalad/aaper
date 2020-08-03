package com.likethesalad.android.aaper.strategies.base

import android.app.Activity
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.strategies.defaults.ActivityRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.AllPermissionStatusProvider

/**
 * Created by César Muñoz on 03/08/20.
 */
abstract class ActivityRequestStrategy : RequestStrategy<Activity>() {

    override fun getRequestLauncher(): RequestLauncher<Activity> {
        return ActivityRequestLauncher()
    }

    override fun getPermissionStatusProvider(): PermissionStatusProvider<Activity> {
        return AllPermissionStatusProvider()
    }
}