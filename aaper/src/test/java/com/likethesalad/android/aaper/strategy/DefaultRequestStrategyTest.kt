package com.likethesalad.android.aaper.strategy

import com.likethesalad.android.aaper.api.data.PermissionsResult
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 13/08/20.
 */

@ExtendWith(MockKExtension::class)
class DefaultRequestStrategyTest {

    @MockK
    lateinit var host: Any

    @MockK
    lateinit var data: PermissionsResult

    private lateinit var defaultRequestStrategy: DefaultRequestStrategy

    @BeforeEach
    fun setUp() {
        defaultRequestStrategy = DefaultRequestStrategy()
    }

    @Test
    fun `Return true if no permissions were denied`() {
        every {
            data.denied
        }.returns(emptyList())

        assertThat(defaultRequestStrategy.onPermissionsRequestResults(host, data))
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

        assertThat(defaultRequestStrategy.onPermissionsRequestResults(host, data)).isFalse()
        verify {
            data.denied
        }
    }
}
