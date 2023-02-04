package com.likethesalad.android.aaper

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyProvider
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
        Aaper.init()

        try {
            Aaper.init()
            fail("Should've gone into the catch block")
        } catch (ignored: AaperInitializedAlreadyException) {
        }
    }

    @Test
    fun `Check default initialization strategy provider`() {
        Aaper.init()

        Truth.assertThat(Aaper.getRequestStrategyProvider())
            .isInstanceOf(DefaultRequestStrategyProvider::class.java)
    }

    @Test
    fun `Set up default strategy provider with default strategy`() {
        val defaultRequestStrategyProvider =
            mockk<DefaultRequestStrategyProvider>(relaxUnitFun = true)
        val strategyCaptor = slot<RequestStrategy<Any>>()

        Aaper.init(defaultRequestStrategyProvider)

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
    fun `Return strategy provider set in the initialization`() {
        val provider = mockk<RequestStrategyProvider>()

        Aaper.init(provider)

        Truth.assertThat(Aaper.getRequestStrategyProvider()).isEqualTo(provider)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun cleanUp() {
        val initializedField = Aaper::class.java.getDeclaredField("initialized")
        initializedField.isAccessible = true
        initializedField.set(null, false)
    }
}