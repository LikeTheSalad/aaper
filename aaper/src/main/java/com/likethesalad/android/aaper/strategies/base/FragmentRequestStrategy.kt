package com.likethesalad.android.aaper.strategies.base

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.strategies.defaults.launchers.FragmentRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.FragmentPermissionStatusProvider

/**
 * Created by César Muñoz on 10/08/20.
 */
abstract class FragmentRequestStrategy : RequestStrategy<Fragment>() {

    override fun getRequestLauncher(host: Fragment): RequestLauncher<Fragment> {
        return FragmentRequestLauncher()
    }

    override fun getPermissionStatusProvider(host: Fragment): PermissionStatusProvider<Fragment> {
        return FragmentPermissionStatusProvider()
    }
}