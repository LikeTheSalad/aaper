package com.likethesalad.android.aaper

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.api.strategy.RequestStrategyFactory
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyFactory
import com.likethesalad.android.aaper.defaults.strategies.DefaultRequestStrategy
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 14/08/20.
 */

class AaperTest {

    private lateinit var permissionManagerMock: PermissionManager

    @Before
    fun setUp() {
        permissionManagerMock = mockk(relaxUnitFun = true)
        PermissionManager.resetForTest()
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
    fun `Set up default strategy factory with default strategy`() {
        val defaultRequestStrategyProvider =
            mockk<DefaultRequestStrategyFactory>(relaxUnitFun = true)
        val strategyCaptor = slot<RequestStrategy<Any>>()

        init(defaultRequestStrategyProvider)

        verify {
            defaultRequestStrategyProvider.register(capture(strategyCaptor))
        }
        verify {
            defaultRequestStrategyProvider.setDefaultStrategyName(DefaultRequestStrategy::class.java.name)
        }
        val strategy = strategyCaptor.captured
        Truth.assertThat(strategy).isInstanceOf(DefaultRequestStrategy::class.java)
    }

    @Test
    fun `Return strategy factory set in the initialization`() {
        val provider = mockk<RequestStrategyFactory>()

        init(provider)

        Truth.assertThat(Aaper.getRequestStrategyFactory<RequestStrategyFactory>())
            .isEqualTo(provider)
    }

    private fun init(provider: RequestStrategyFactory = DefaultRequestStrategyFactory()) {
        if (provider is DefaultRequestStrategyFactory) {
            provider.register(DefaultRequestStrategy())
            provider.setDefaultStrategyName(DefaultRequestStrategy.NAME)
        }
        Aaper.setUp(mockk(), provider)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun cleanUp() {
        val initializedField = Aaper::class.java.getDeclaredField("initialized")
        initializedField.isAccessible = true
        initializedField.set(null, false)
    }
}