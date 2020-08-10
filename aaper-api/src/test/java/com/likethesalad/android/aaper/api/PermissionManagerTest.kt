package com.likethesalad.android.aaper.api

import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.internal.base.RequestStrategyProviderSource
import com.likethesalad.android.aaper.internal.utils.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * Created by César Muñoz on 10/08/20.
 */
class PermissionManagerTest : BaseMockable() {

    @MockK
    lateinit var strategyProvider: RequestStrategyProvider

    @MockK
    lateinit var host: Any

    @MockK
    lateinit var originalMethod: Runnable

    @MockK
    lateinit var permissionStatusProvider: PermissionStatusProvider<Any>

    @MockK
    lateinit var requestLauncher: RequestLauncher<Any>

    @MockK
    lateinit var strategy: RequestStrategy<Any>

    private val strategyName = "someStrategyName"

    companion object {

        private val sourceMock: RequestStrategyProviderSource = mockk(relaxUnitFun = true)

        @JvmStatic
        @BeforeClass
        fun init() {
            PermissionManager.setStrategyProviderSource(sourceMock)
        }
    }

    @Before
    fun setUp() {
        every { sourceMock.getRequestStrategyProvider() }.returns(strategyProvider)
        every { strategyProvider.getStrategy(host, strategyName) }.returns(strategy)
        every {
            strategy.internalGetPermissionStatusProvider(host)
        }.returns(permissionStatusProvider)
        every { strategy.internalGetRequestLauncher(host) }.returns(requestLauncher)
    }

    @Test
    fun `Process request with missing permissions and no pre-processing`() {
        val permissions = arrayOf("one", "two", "three", "four")
        val missingPermissions = arrayOf("one", "two")
        setUpPermissions(permissions, missingPermissions)
        every {
            strategy.internalOnBeforeLaunchingRequest(host, any(), any())
        }.returns(false)

        PermissionManager.processPermissionRequest(host, permissions, originalMethod, strategyName)
    }

    private fun setUpPermissions(permissions: Array<String>, missingPermissions: Array<String>) {
        permissions.forEach {
            every {
                permissionStatusProvider.internalIsPermissionGranted(host, it)
            }.returns(it !in missingPermissions)
        }
    }
}