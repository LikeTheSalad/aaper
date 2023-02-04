package com.likethesalad.android.aaper.sample.test.testutils.strategies

import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.base.strategies.impl.AllRequestStrategy
import com.likethesalad.android.aaper.sample.test.testutils.launcher.TestRequestLauncher

class AlwaysSuccessfulStrategy : AllRequestStrategy() {

    companion object {
        const val NAME = "all-success"
    }

    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        return true
    }

    override fun getRequestLauncher(host: Any): RequestLauncher<Any> {
        return TestRequestLauncher()
    }

    override fun getName(): String {
        return NAME
    }
}