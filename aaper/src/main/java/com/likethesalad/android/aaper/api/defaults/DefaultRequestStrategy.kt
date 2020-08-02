package com.likethesalad.android.aaper.api.defaults

import android.app.Activity
import com.likethesalad.android.aaper.api.base.ActivityRequestStrategy

/**
 * Created by César Muñoz on 29/07/20.
 */
class DefaultRequestStrategy(activity: Activity) :
    ActivityRequestStrategy(activity) {

    companion object {
        val NAME = DefaultRequestStrategy::class.java.name
    }

    override fun getName(): String {
        return NAME
    }

    override fun onPermissionsRequestResults(
        permissionsGranted: List<String>,
        permissionsDenied: List<String>
    ): Boolean {
        // If all permissions were accepted, return true so that the annotated method
        // gets called.
        return permissionsDenied.isEmpty()
    }
}