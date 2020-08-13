package com.likethesalad.android.aaper.defaults.strategies

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by César Muñoz on 13/08/20.
 */

@RunWith(MockitoJUnitRunner::class)
class DefaultRequestStrategyTest {

    @Mock
    lateinit var host: Any

    @Mock
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
        whenever(data.denied).thenReturn(emptyList())

        Truth.assertThat(defaultRequestStrategy.onPermissionsRequestResults(host, data))
            .isTrue()
        verify(data).denied
    }

    @Test
    fun `Return false if at least one permissions was denied`() {
        whenever(data.denied).thenReturn(listOf("one"))

        Truth.assertThat(defaultRequestStrategy.onPermissionsRequestResults(host, data))
            .isFalse()
        verify(data).denied
    }
}