package com.likethesalad.android.aaper.api

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.api.base.RequestStrategyProvider
import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.internal.base.RequestStrategyProviderSource
import com.likethesalad.android.aaper.internal.utils.RequestRunner
import com.likethesalad.android.aaper.internal.utils.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
    lateinit var originalMethod: Runnable

    @MockK
    lateinit var permissionStatusProvider: PermissionStatusProvider<Any>

    @MockK
    lateinit var requestLauncher: RequestLauncher<Any>

    @MockK
    lateinit var strategy: RequestStrategy<Any>


    private val host = Any()
    private val requestCode = 1202
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
        every { strategy.getRequestCode() }.returns(requestCode)
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

        verify {
            requestLauncher.internalLaunchPermissionsRequest(
                host,
                missingPermissions.toList(),
                requestCode
            )
        }
    }

    @Test
    fun `Process request with missing permissions and pre-processing`() {
        val permissions = arrayOf("one", "two", "three", "four")
        val missingPermissions = arrayOf("one", "two")
        val dataCaptor = slot<PermissionsRequest>()
        val runnerCaptor = slot<RequestRunner>()
        setUpPermissions(permissions, missingPermissions)
        every {
            strategy.internalOnBeforeLaunchingRequest(
                host, capture(dataCaptor), capture(runnerCaptor)
            )
        }.returns(true)

        PermissionManager.processPermissionRequest(host, permissions, originalMethod, strategyName)

        val capturedData = dataCaptor.captured
        Truth.assertThat(capturedData.missingPermissions).isEqualTo(missingPermissions.toList())
        Truth.assertThat(capturedData.permissions).isEqualTo(permissions.toList())
        verify(exactly = 0) {
            requestLauncher.internalLaunchPermissionsRequest(
                host,
                missingPermissions.toList(),
                requestCode
            )
        }

        // Check runnable
        runnerCaptor.captured.run()
        verify {
            requestLauncher.internalLaunchPermissionsRequest(
                host,
                missingPermissions.toList(),
                requestCode
            )
        }
    }

    private fun setUpPermissions(permissions: Array<String>, missingPermissions: Array<String>) {
        permissions.forEach {
            every {
                permissionStatusProvider.internalIsPermissionGranted(host, it)
            }.returns(it !in missingPermissions)
        }
    }
}