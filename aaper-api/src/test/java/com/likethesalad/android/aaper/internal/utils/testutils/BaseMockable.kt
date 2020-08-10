package com.likethesalad.android.aaper.internal.utils.testutils

import io.mockk.MockKAnnotations
import org.junit.Before

/**
 * Created by César Muñoz on 10/08/20.
 */

open class BaseMockable {

    @Before
    fun setUpMockk() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    inline fun <reified T : Any> mockk() = io.mockk.mockk<T>(relaxUnitFun = true)
}