package com.likethesalad.android.aaper.strategy

import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.strategy.impl.AllRequestStrategy

/**
 * This is the default strategy to handle both Activity and Fragment's permissions requests.
 * It extends from [AllRequestStrategy] so that it can handle both types of hosts. Regarding
 * the response handling, it simply checks the denied permissions list, if it's empty, it returns
 * true so that the annotated method gets called, false otherwise (avoiding the annotated method
 * to get called).
 */
class DefaultRequestStrategy : AllRequestStrategy() {

    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        // If no permissions were denied, return true so that the annotated method
        // gets called.
        return data.denied.isEmpty()
    }
}