package com.likethesalad.android.aaper.defaults

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.errors.StrategyNameAlreadyExistsException
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 14/08/20.
 */

class DefaultRequestStrategyProviderTest : BaseMockable() {

    @MockK
    lateinit var host: Any

    private lateinit var defaultRequestStrategyProvider: DefaultRequestStrategyProvider

    @Before
    fun setUp() {
        defaultRequestStrategyProvider = DefaultRequestStrategyProvider()
    }

    @Test
    fun `Register strategy by name`() {
        val strategyName = "someStrategy"
        val strategy = createStrategyMock(strategyName)

        defaultRequestStrategyProvider.register(strategy)

        Truth.assertThat(getStrategyForName(strategyName)).isEqualTo(strategy)
    }

    @Test
    fun `Verify exception when trying to register a strategy with an already existing name`() {
        val strategyName = "someStrategy"
        val strategy = createStrategyMock(strategyName)
        val anotherStrategy = createStrategyMock(strategyName)
        defaultRequestStrategyProvider.register(strategy)

        try {
            defaultRequestStrategyProvider.register(anotherStrategy)
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
        defaultRequestStrategyProvider.register(strategy, strategy2)
        defaultRequestStrategyProvider.setDefaultStrategyName(strategyName2)

        val result = getDefaultStrategy()

        Truth.assertThat(result).isEqualTo(strategy2)
        Truth.assertThat(getStrategyForName(strategyName)).isEqualTo(strategy)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getStrategyForName(name: String): RequestStrategy<out Any> {
        val method = defaultRequestStrategyProvider.javaClass.getDeclaredMethod(
            "getStrategyForName",
            Any::class.java,
            String::class.java
        )
        return method.invoke(defaultRequestStrategyProvider, host, name) as RequestStrategy<out Any>
    }

    @Suppress("UNCHECKED_CAST")
    private fun getDefaultStrategy(): RequestStrategy<out Any> {
        val method = defaultRequestStrategyProvider.javaClass.getDeclaredMethod(
            "getDefaultStrategy",
            Any::class.java
        )
        return method.invoke(defaultRequestStrategyProvider, host) as RequestStrategy<out Any>
    }

    private fun createStrategyMock(name: String): RequestStrategy<Any> {
        val strategy = mockk<RequestStrategy<Any>>()
        every {
            strategy.getName()
        }.returns(name)
        return strategy
    }
}