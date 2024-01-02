package com.likethesalad.android.aaper.sample.custom

import android.app.Activity
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.base.activity.strategy.ActivityRequestStrategy

/**
 * Example of a RequestStrategy that closes the host Activity if at least one permission
 * is denied. Take a look at the README.md for more information.
 */
class FinishActivityOnDeniedStrategy : ActivityRequestStrategy() {

    override fun onPermissionsRequestResults(
        host: Activity,
        data: PermissionsResult
    ): Boolean {
        if (data.denied.isNotEmpty()) {
            // At least one permission was denied.
            host.finish()
            return false // So that the annotated method doesn't get called.
        }

        // No permissions were denied, therefore proceed to call the annotated method.
        return true
    }
}