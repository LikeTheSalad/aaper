package com.likethesalad.android.aaper.api.base

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory
import com.likethesalad.android.aaper.testutils.StrategyTest
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 10/08/20.
 */
class RequestStrategyFactoryTest : BaseMockable() {

    @MockK
    lateinit var strategy: StrategyTest

    @MockK
    lateinit var host: Any

    private lateinit var requestStrategyProvider: TestRequestStrategyFactory

    @Before
    fun setUp() {
        requestStrategyProvider = TestRequestStrategyFactory(strategy)
    }

    @Test
    fun `Get strategy by type`() {
        val result = requestStrategyProvider.getStrategy(host, StrategyTest::class.java)

        Truth.assertThat(result).isEqualTo(strategy)
        Truth.assertThat(requestStrategyProvider.forHost).isEqualTo(host)
        Truth.assertThat(requestStrategyProvider.forType).isEqualTo(StrategyTest::class.java)
    }

    class TestRequestStrategyFactory(private val strategy: RequestStrategy<Any>) :
        RequestStrategyFactory {

        var forHost: Any? = null
        var forType: Class<out RequestStrategy<out Any>>? = null

        @Suppress("UNCHECKED_CAST")
        override fun <T : RequestStrategy<out Any>> getStrategy(host: Any, type: Class<T>): T {
            forHost = host
            forType = type
            return strategy as T
        }
    }
}