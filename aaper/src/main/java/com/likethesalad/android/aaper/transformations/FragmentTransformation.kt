package com.likethesalad.android.aaper.transformations

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.transformations.base.AndroidComponentBaseTransformation

/**
 * ByteBuddy transformation for Fragments.
 */
class FragmentTransformation : AndroidComponentBaseTransformation() {

    override fun getHostClassType(): Class<out Any> {
        return Fragment::class.java
    }
}