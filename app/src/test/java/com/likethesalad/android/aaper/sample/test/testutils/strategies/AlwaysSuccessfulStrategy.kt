package com.likethesalad.android.aaper.sample.test.testutils.strategies

import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.base.strategies.impl.AllRequestStrategy

class AlwaysSuccessfulStrategy : AllRequestStrategy() {

    companion object {
        const val NAME = "all-success"
    }

    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        return true
    }

    override fun getName(): String {
        return NAME
    }
}