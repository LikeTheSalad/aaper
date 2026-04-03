package com.likethesalad.android.aaper.launcher

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 13/08/20.
 */

@ExtendWith(MockKExtension::class)
class RequestWithCodeLauncherTest {

    @MockK
    lateinit var host: Any

    private lateinit var launcher: TestRequestWithCodeLauncher

    @BeforeEach
    fun setUp() {
        launcher = TestRequestWithCodeLauncher()
    }

    @Test
    fun `Extract request code from metadata and pass it to the delegated method`() {
        val requestCode = 2020
        val permissions = listOf("one", "two")

        launcher.launchPermissionsRequest(host, permissions, requestCode)

        assertThat(launcher.requestCodePassed).isEqualTo(requestCode)
    }

    class TestRequestWithCodeLauncher : RequestWithCodeLauncher<Any>() {

        var requestCodePassed = -1

        override fun launchPermissionsRequest(
            host: Any,
            permissions: List<String>,
            requestCode: Int
        ) {
            requestCodePassed = requestCode
        }
    }
}
