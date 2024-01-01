package com.likethesalad.android.aaper.internal.utils

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.errors.RequestExecutedAlreadyException
import com.likethesalad.android.aaper.internal.data.PendingRequest
import com.likethesalad.android.aaper.internal.utils.testutils.BaseMockable
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 10/08/20.
 */

class RequestRunnerTest : BaseMockable() {

    @MockK
    lateinit var functionMock: (PendingRequest) -> Unit

    @MockK
    lateinit var hostMock: Any

    @MockK
    lateinit var strategyMock: RequestStrategy<Any>

    @MockK
    lateinit var originalMethodRunnable: Runnable

    private val permissions = listOf("one", "two", "three", "four")
    private val missingPermissions = listOf("one", "two")

    private lateinit var pendingRequest: PendingRequest
    private lateinit var requestRunner: RequestRunner

    @Before
    fun setUp() {
        every { functionMock.invoke(any()) } just Runs
        pendingRequest = createPendingRequest()
        requestRunner = RequestRunner(pendingRequest, functionMock)
    }

    @Test
    fun `Check invoke function at first call`() {
        requestRunner.run()

        verify {
            functionMock.invoke(pendingRequest)
        }
    }

    @Test
    fun `Crash when running more than once`() {
        requestRunner.run()

        try {
            requestRunner.run()
            fail("Should have gone into the catch block")
        } catch (e: RequestExecutedAlreadyException) {
            Truth.assertThat(e.permissions).isEqualTo(permissions)
        }
    }

    private fun createPendingRequest(): PendingRequest {
        return PendingRequest(
            hostMock,
            createPermissionsRequest(),
            strategyMock,
            originalMethodRunnable
        )
    }

    private fun createPermissionsRequest(): PermissionsRequest {
        return PermissionsRequest(permissions, missingPermissions)
    }
}