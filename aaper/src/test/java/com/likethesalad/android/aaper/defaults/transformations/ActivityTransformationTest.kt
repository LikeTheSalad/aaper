package com.likethesalad.android.aaper.defaults.transformations

import android.app.Activity
import com.google.common.truth.Truth
import org.junit.Test

/**
 * Created by César Muñoz on 13/08/20.
 */

class ActivityTransformationTest {

    @Test
    fun `Verify class type`() {
        val transformation = ActivityTransformation()

        Truth.assertThat(transformation.getHostClassType()).isEqualTo(Activity::class.java)
    }
}