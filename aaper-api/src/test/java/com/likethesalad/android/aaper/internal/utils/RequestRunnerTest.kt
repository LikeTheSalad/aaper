package com.likethesalad.android.aaper.internal.utils

import com.likethesalad.android.aaper.api.data.PermissionsRequest
import com.likethesalad.android.aaper.api.errors.RequestExecutedAlreadyException
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.internal.data.PendingRequest
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 10/08/20.
 */

@ExtendWith(MockKExtension::class)
class RequestRunnerTest {

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

    @BeforeEach
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
            assertThat(e.permissions).isEqualTo(permissions)
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
