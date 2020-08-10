package com.likethesalad.android.aaper.strategies.base

import android.app.Activity
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.strategies.defaults.launchers.ActivityRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.launchers.FragmentRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.ActivityPermissionStatusProvider
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.FragmentPermissionStatusProvider

/**
 * Created by César Muñoz on 10/08/20.
 */
abstract class AllRequestStrategy : RequestStrategy<Any>() {

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