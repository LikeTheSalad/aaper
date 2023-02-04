package com.likethesalad.android.aaper.defaults.strategies

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 13/08/20.
 */

class DefaultRequestStrategyTest : BaseMockable() {

    @MockK
    lateinit var host: Any

    @MockK
    lateinit var data: PermissionsResult

    private lateinit var defaultRequestStrategy: DefaultRequestStrategy

    @Before
    fun setUp() {
        defaultRequestStrategy = DefaultRequestStrategy()
    }

    @Test
    fun `Verify name`() {
        Truth.assertThat(defaultRequestStrategy.getName())
            .isEqualTo("com.likethesalad.android.aaper.defaults.strategies.DefaultRequestStrategy")
    }

    @Test
    fun `Return true if no permissions were denied`() {
        every {
            data.denied
        }.returns(emptyList())

        Truth.assertThat(defaultRequestStrategy.onPermissionsRequestResults(host, data))
            .isTrue()
        verify {
            data.denied
        }
    }

    @Test
    fun `Return false if at least one permissions was denied`() {
        every {
            data.denied
        }.returns(listOf("one"))

        Truth.assertThat(defaultRequestStrategy.onPermissionsRequestResults(host, data)).isFalse()
        verify {
            data.denied
        }
    }
}