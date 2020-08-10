package com.likethesalad.android.aaper.transformations

import android.app.Activity
import com.likethesalad.android.aaper.api.base.bytebuddy.AndroidComponentBaseTransformation

/**
 * Created by César Muñoz on 10/08/20.
 */
class ActivityTransformation : AndroidComponentBaseTransformation() {

    override fun getHostClassType(): Class<out Any> {
        return Activity::class.java
    }
}