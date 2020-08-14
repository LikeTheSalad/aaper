package com.likethesalad.android.aaper.defaults.transformations

import androidx.fragment.app.Fragment
import com.google.common.truth.Truth
import org.junit.Test

/**
 * Created by César Muñoz on 14/08/20.
 */
class FragmentTransformationTest {

    @Test
    fun `Verify class type`() {
        val transformation = FragmentTransformation()

        Truth.assertThat(transformation.getHostClassType()).isEqualTo(Fragment::class.java)
    }
}