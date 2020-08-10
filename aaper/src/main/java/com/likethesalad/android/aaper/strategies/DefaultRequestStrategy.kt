package com.likethesalad.android.aaper.strategies

import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.strategies.base.AllRequestStrategy

/**
 * Created by César Muñoz on 03/08/20.
 */
class DefaultRequestStrategy : AllRequestStrategy() {

    companion object {
        internal val NAME = DefaultRequestStrategy::class.java.name
    }

    override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
        // If no permissions were denied, return true so that the annotated method
        // gets called.
        return data.denied.isEmpty()
    }

    override fun getName(): String = NAME
}