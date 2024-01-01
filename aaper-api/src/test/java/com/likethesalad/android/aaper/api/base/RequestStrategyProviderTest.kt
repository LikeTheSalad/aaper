package com.likethesalad.android.aaper.api.base

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategyProvider
import com.likethesalad.android.aaper.internal.utils.testutils.BaseMockable
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 10/08/20.
 */
class RequestStrategyProviderTest : BaseMockable() {

    @MockK
    lateinit var strategy: RequestStrategy<Any>

    @MockK
    lateinit var host: Any

    private lateinit var requestStrategyProvider: TestRequestStrategyProvider

    @Before
    fun setUp() {
        requestStrategyProvider = TestRequestStrategyProvider(strategy)
    }

    @Test
    fun `Get strategy by name when not default is provided`() {
        val name = "someName"

        val result = requestStrategyProvider.getStrategy(host, name)

        Truth.assertThat(result).isEqualTo(strategy)
        Truth.assertThat(requestStrategyProvider.defaultHost).isNull()
        Truth.assertThat(requestStrategyProvider.forNameHost).isEqualTo(host)
        Truth.assertThat(requestStrategyProvider.forNameName).isEqualTo(name)
    }

    @Test
    fun `Get default strategy when default name is provided`() {
        val name = "[default.strategy]"

        val result = requestStrategyProvider.getStrategy(host, name)

        Truth.assertThat(result).isEqualTo(strategy)
        Truth.assertThat(requestStrategyProvider.forNameHost).isNull()
        Truth.assertThat(requestStrategyProvider.forNameName).isNull()
        Truth.assertThat(requestStrategyProvider.defaultHost).isEqualTo(host)
    }

    class TestRequestStrategyProvider(private val strategy: RequestStrategy<Any>) :
        RequestStrategyProvider() {

        var forNameHost: Any? = null
        var forNameName: String? = null
        var defaultHost: Any? = null

        override fun getStrategyForName(host: Any, name: String): RequestStrategy<out Any> {
            forNameHost = host
            forNameName = name
            return strategy
        }

        override fun getDefaultStrategy(host: Any): RequestStrategy<out Any> {
            defaultHost = host
            return strategy
        }
    }
}