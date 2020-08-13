package com.likethesalad.android.aaper.strategies.base

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.launchers.FragmentRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.FragmentPermissionStatusProvider

/**
 * Base class to create a strategy for Fragments-only as host, meaning that only the annotated
 * functions on Fragments can be handled by this Strategy. If for example, there's an attempt
 * to use this strategy to handle annotated functions on a non-Fragment class, e.g. An Activity then
 * this will cause the app to crash. In order to create a Strategy that works for both, Activities
 * and Fragments, you must extend from [AllRequestStrategy] instead.
 */
abstract class FragmentRequestStrategy : RequestWithCodeMetadataStrategy<Fragment>() {

    override fun getRequestLauncher(host: Fragment): RequestLauncher<Fragment> {
        return FragmentRequestLauncher()
    }

    override fun getPermissionStatusProvider(host: Fragment): PermissionStatusProvider<Fragment> {
        return FragmentPermissionStatusProvider()
    }
}