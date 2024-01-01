package com.likethesalad.android.aaper

import android.content.Context
import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.strategy.NoopRequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import com.likethesalad.android.aaper.strategy.DefaultRequestStrategy
import com.likethesalad.android.aaper.strategy.DefaultRequestStrategyFactory
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 14/08/20.
 */

class AaperTest : BaseMockable() {

    @MockK
    lateinit var applicationContext: Context

    @Before
    fun setUp() {
        every { applicationContext.applicationContext }.returns(applicationContext)
    }

    @After
    fun tearDown() {
        cleanUp()
    }

    @Test
    fun `Check initialization valid once only`() {
        init()

        try {
            init()
            fail("Should've gone into the catch block")
        } catch (ignored: AaperInitializedAlreadyException) {
        }
    }

    @Test
    fun `Check default initialization strategy factory`() {
        init()

        Truth.assertThat(Aaper.getRequestStrategyFactory<DefaultRequestStrategyFactory>())
            .isInstanceOf(DefaultRequestStrategyFactory::class.java)
    }

    @Test
    fun `Setting a strategy factory only once`() {
        init()

        val factory = TestStrategyFactory()
        Aaper.setRequestStrategyFactory(factory)

        Truth.assertThat(Aaper.getRequestStrategyFactory<TestStrategyFactory>())
            .isInstanceOf(TestStrategyFactory::class.java)

        try {
            Aaper.setRequestStrategyFactory(factory)
        } catch (e: IllegalStateException) {
            Truth.assertThat(e.message)
                .isEqualTo("The RequestStrategyFactory instance can only be set once.")
        }
    }

    @Test
    fun `Verify default strategy`() {
        init()

        Truth.assertThat(Aaper.getDefaultStrategyType() == DefaultRequestStrategy::class.java)
            .isTrue()
    }

    @Test
    fun `Setting a default strategy`() {
        init()

        Aaper.setDefaultStrategyType(TestStrategy::class.java)

        Truth.assertThat(Aaper.getDefaultStrategyType() == TestStrategy::class.java)
            .isTrue()
    }

    private fun init() {
        Aaper.initialize(applicationContext)
    }

    private fun cleanUp() {
        PermissionManager.resetForTest()
        Aaper.resetForTest()
    }

    class TestStrategy : NoopRequestStrategy()

    class TestStrategyFactory : RequestStrategyFactory {
        override fun <T : RequestStrategy<out Any>> getStrategy(host: Any, type: Class<T>): T {
            throw UnsupportedOperationException()
        }
    }
}