package com.likethesalad.android.aaper.transformations

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.base.bytebuddy.AndroidComponentBaseTransformation

/**
 * ByteBuddy transformation for Activities.
 */
class FragmentTransformation : AndroidComponentBaseTransformation() {

    override fun getHostClassType(): Class<out Any> {
        return Fragment::class.java
    }
}