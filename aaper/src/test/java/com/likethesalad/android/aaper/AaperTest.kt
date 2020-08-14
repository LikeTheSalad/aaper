package com.likethesalad.android.aaper

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.PermissionManager
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyProvider
import com.likethesalad.android.aaper.defaults.strategies.DefaultRequestStrategy
import com.likethesalad.android.aaper.errors.AaperInitializedAlreadyException
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

/**
 * Created by César Muñoz on 14/08/20.
 */

@RunWith(PowerMockRunner::class)
@PrepareForTest(PermissionManager::class)
class AaperTest {

    private lateinit var permissionManagerMock: PermissionManager

    @Before
    fun setUp() {
        permissionManagerMock = mock()
        Whitebox.setInternalState(
            PermissionManager::class.java, "INSTANCE", permissionManagerMock
        )
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
        verify(permissionManagerMock).setStrategyProviderSource(Aaper)
    }

    @Test
    fun `Set up default strategy provider with default strategy`() {
        val defaultRequestStrategyProvider = mock<DefaultRequestStrategyProvider>()
        val strategyCaptor = argumentCaptor<RequestStrategy<Any>>()

        Aaper.init(defaultRequestStrategyProvider)

        verify(defaultRequestStrategyProvider).register(strategyCaptor.capture())
        verify(defaultRequestStrategyProvider)
            .setDefaultStrategyName(DefaultRequestStrategy::class.java.name)
        val strategy = strategyCaptor.firstValue
        Truth.assertThat(strategy).isInstanceOf(DefaultRequestStrategy::class.java)
    }

    @Test
    fun `Return strategy provider set in the initialization`() {
        val provider = mock<RequestStrategyProvider>()

        Aaper.init(provider)

        Truth.assertThat(Aaper.getRequestStrategyProvider()).isEqualTo(provider)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    private fun cleanUp() {
        Whitebox.setInternalState(Aaper.javaClass, "initialized", false)
        Whitebox.setInternalState(
            Aaper.javaClass,
            "strategyProvider",
            null as? RequestStrategyProvider
        )
    }
}