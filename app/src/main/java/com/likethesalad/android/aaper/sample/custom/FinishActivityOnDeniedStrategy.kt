package com.likethesalad.android.aaper.sample.custom

import android.app.Activity
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.base.strategies.impl.ActivityRequestStrategy

/**
 * Created by César Muñoz on 15/08/20.
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

    override fun getName(): String {
        // We can return anything here, as long as there is no other Strategy with the same
        // name.
        return "FinishActivityOnDenied"
    }
}