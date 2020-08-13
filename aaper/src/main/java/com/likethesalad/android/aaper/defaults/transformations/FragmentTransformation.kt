package com.likethesalad.android.aaper.defaults.transformations

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.base.transformations.bytebuddy.AndroidComponentBaseTransformation

/**
 * ByteBuddy transformation for Fragments.
 */
class FragmentTransformation : AndroidComponentBaseTransformation() {

    override fun getHostClassType(): Class<out Any> {
        return Fragment::class.java
    }
}