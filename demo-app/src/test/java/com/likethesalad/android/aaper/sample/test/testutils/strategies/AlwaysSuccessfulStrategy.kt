package com.likethesalad.android.aaper.sample.test.testutils.strategies

import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.api.launcher.RequestLauncher
import com.likethesalad.android.aaper.base.common.strategy.AllRequestStrategy
import com.likethesalad.android.aaper.sample.test.testutils.launcher.TestRequestLauncher

class AlwaysSuccessfulStrategy : AllRequestStrategy() {

    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        return true
    }

    override fun getRequestLauncher(host: Any): RequestLauncher<Any> {
        return TestRequestLauncher()
    }
}