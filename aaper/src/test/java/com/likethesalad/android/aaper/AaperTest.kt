package com.likethesalad.android.aaper

import android.content.Context
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.strategy.NoopRequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import com.likethesalad.android.aaper.strategy.DefaultRequestStrategy
import com.likethesalad.android.aaper.strategy.DefaultRequestStrategyFactory
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 14/08/20.
 */

@ExtendWith(MockKExtension::class)
class AaperTest {

    @MockK
    lateinit var applicationContext: Context

    @BeforeEach
    fun setUp() {
        every { applicationContext.applicationContext }.returns(applicationContext)
    }

    @AfterEach
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

        assertThat(Aaper.getRequestStrategyFactory<DefaultRequestStrategyFactory>())
            .isInstanceOf(DefaultRequestStrategyFactory::class.java)
    }

    @Test
    fun `Setting a strategy factory only once`() {
        init()

        val factory = TestStrategyFactory()
        Aaper.setRequestStrategyFactory(factory)

        assertThat(Aaper.getRequestStrategyFactory<TestStrategyFactory>())
            .isInstanceOf(TestStrategyFactory::class.java)

        try {
            Aaper.setRequestStrategyFactory(factory)
        } catch (e: IllegalStateException) {
            assertThat(e.message)
                .isEqualTo("The RequestStrategyFactory instance can only be set once.")
        }
    }

    @Test
    fun `Verify default strategy`() {
        init()

        assertThat(Aaper.getDefaultStrategy() == DefaultRequestStrategy::class.java)
            .isTrue()
    }

    @Test
    fun `Setting a default strategy`() {
        init()

        Aaper.setDefaultStrategy(TestStrategy::class.java)

        assertThat(Aaper.getDefaultStrategy() == TestStrategy::class.java)
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
