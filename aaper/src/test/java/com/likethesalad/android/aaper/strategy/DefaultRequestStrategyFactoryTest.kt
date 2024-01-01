package com.likethesalad.android.aaper.strategy

import android.content.Context
import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.strategy.NoopRequestStrategy
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 14/08/20.
 */

class DefaultRequestStrategyFactoryTest : BaseMockable() {

    @MockK
    lateinit var host: Any

    @MockK
    lateinit var applicationContext: Context

    private lateinit var defaultRequestStrategyFactory: DefaultRequestStrategyFactory

    @Before
    fun setUp() {
        defaultRequestStrategyFactory = DefaultRequestStrategyFactory(applicationContext)
    }

    @Test
    fun `Provide Strategy with empty constructor`() {
        val strategy =
            defaultRequestStrategyFactory.getStrategy(host, DefaultRequestStrategy::class.java)

        Truth.assertThat(strategy).isInstanceOf(DefaultRequestStrategy::class.java)
    }

    @Test
    fun `Provide Strategy with constructor that has an app Context param`() {
        val strategy =
            defaultRequestStrategyFactory.getStrategy(host, TestStrategyWithContext::class.java)

        Truth.assertThat(strategy).isInstanceOf(TestStrategyWithContext::class.java)
        Truth.assertThat(strategy.appContext).isEqualTo(applicationContext)
    }

    class TestStrategyWithContext(val appContext: Context) : NoopRequestStrategy()
}