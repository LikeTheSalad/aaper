package com.likethesalad.android.aaper.strategy

import android.content.Context
import com.likethesalad.android.aaper.api.strategy.NoopRequestStrategy
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 14/08/20.
 */

@ExtendWith(MockKExtension::class)
class DefaultRequestStrategyFactoryTest {

    @MockK
    lateinit var host: Any

    @MockK
    lateinit var applicationContext: Context

    private lateinit var defaultRequestStrategyFactory: DefaultRequestStrategyFactory

    @BeforeEach
    fun setUp() {
        defaultRequestStrategyFactory = DefaultRequestStrategyFactory(applicationContext)
    }

    @Test
    fun `Provide Strategy with empty constructor`() {
        val strategy =
            defaultRequestStrategyFactory.getStrategy(host, DefaultRequestStrategy::class.java)

        assertThat(strategy).isInstanceOf(DefaultRequestStrategy::class.java)
    }

    @Test
    fun `Provide Strategy with constructor that has an app Context param`() {
        val strategy =
            defaultRequestStrategyFactory.getStrategy(host, TestStrategyWithContext::class.java)

        assertThat(strategy).isInstanceOf(TestStrategyWithContext::class.java)
        assertThat(strategy.appContext).isEqualTo(applicationContext)
    }

    class TestStrategyWithContext(val appContext: Context) : NoopRequestStrategy()
}
