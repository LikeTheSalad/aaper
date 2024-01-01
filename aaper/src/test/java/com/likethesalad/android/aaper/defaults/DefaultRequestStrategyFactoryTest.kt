package com.likethesalad.android.aaper.defaults

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 14/08/20.
 */

class DefaultRequestStrategyFactoryTest : BaseMockable() {

    @MockK
    lateinit var host: Any

    private lateinit var defaultRequestStrategyFactory: DefaultRequestStrategyFactory

    @Before
    fun setUp() {
        defaultRequestStrategyFactory = DefaultRequestStrategyFactory()
    }

    @Test
    fun `Register strategy by name`() {
        val strategyName = "someStrategy"
        val strategy = createStrategyMock(strategyName)

        defaultRequestStrategyFactory.register(strategy)

        Truth.assertThat(getStrategyForName(strategyName)).isEqualTo(strategy)
    }

    @Test
    fun `Verify exception when trying to register a strategy with an already existing name`() {
        val strategyName = "someStrategy"
        val strategy = createStrategyMock(strategyName)
        val anotherStrategy = createStrategyMock(strategyName)
        defaultRequestStrategyFactory.register(strategy)

        try {
            defaultRequestStrategyFactory.register(anotherStrategy)
            fail("Should've gone into the catch block")
        } catch (e: StrategyNameAlreadyExistsException) {
            Truth.assertThat(e.strategyName).isEqualTo(strategyName)
        }
    }

    @Test
    fun `Verify default strategy`() {
        val strategyName = "someName"
        val strategyName2 = "someName2"
        val strategy = createStrategyMock(strategyName)
        val strategy2 = createStrategyMock(strategyName2)
        defaultRequestStrategyFactory.register(strategy, strategy2)
        defaultRequestStrategyFactory.setDefaultStrategyName(strategyName2)

        val result = getDefaultStrategy()

        Truth.assertThat(result).isEqualTo(strategy2)
        Truth.assertThat(getStrategyForName(strategyName)).isEqualTo(strategy)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getStrategyForName(name: String): RequestStrategy<out Any> {
        val method = defaultRequestStrategyFactory.javaClass.getDeclaredMethod(
            "getStrategyForName",
            Any::class.java,
            String::class.java
        )
        return method.invoke(defaultRequestStrategyFactory, host, name) as RequestStrategy<out Any>
    }

    @Suppress("UNCHECKED_CAST")
    private fun getDefaultStrategy(): RequestStrategy<out Any> {
        val method = defaultRequestStrategyFactory.javaClass.getDeclaredMethod(
            "getDefaultStrategy",
            Any::class.java
        )
        return method.invoke(defaultRequestStrategyFactory, host) as RequestStrategy<out Any>
    }

    private fun createStrategyMock(name: String): RequestStrategy<Any> {
        val strategy = mockk<RequestStrategy<Any>>()
        every {
            strategy.getName()
        }.returns(name)
        return strategy
    }
}