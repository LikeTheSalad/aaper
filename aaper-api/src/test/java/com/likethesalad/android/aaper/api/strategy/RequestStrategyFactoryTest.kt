package com.likethesalad.android.aaper.api.strategy

import com.likethesalad.android.aaper.testutils.StrategyTest
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 10/08/20.
 */
@ExtendWith(MockKExtension::class)
class RequestStrategyFactoryTest {

    @MockK
    lateinit var strategy: StrategyTest

    @MockK
    lateinit var host: Any

    private lateinit var requestStrategyProvider: TestRequestStrategyFactory

    @BeforeEach
    fun setUp() {
        requestStrategyProvider = TestRequestStrategyFactory(strategy)
    }

    @Test
    fun `Get strategy by type`() {
        val result = requestStrategyProvider.getStrategy(host, StrategyTest::class.java)

        assertThat(result).isEqualTo(strategy)
        assertThat(requestStrategyProvider.forHost).isEqualTo(host)
        assertThat(requestStrategyProvider.forType).isEqualTo(StrategyTest::class.java)
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
