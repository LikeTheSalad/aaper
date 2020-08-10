package com.likethesalad.android.aaper.transformations

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.base.bytebuddy.AndroidComponentBaseTransformation

/**
 * Created by César Muñoz on 10/08/20.
 */
class FragmentTransformation : AndroidComponentBaseTransformation() {

    override fun getSupportedClassType(): Class<out Any> {
        return Fragment::class.java
    }
}