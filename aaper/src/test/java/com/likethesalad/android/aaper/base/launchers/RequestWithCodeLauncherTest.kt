package com.likethesalad.android.aaper.base.launchers

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by César Muñoz on 13/08/20.
 */

@RunWith(MockitoJUnitRunner::class)
class RequestWithCodeLauncherTest {

    @Mock
    lateinit var host: Any

    private lateinit var launcher: TestRequestWithCodeLauncher

    @Before
    fun setUp() {
        launcher = TestRequestWithCodeLauncher()
    }

    @Test
    fun `Extract request code from metadata and pass it to the delegated method`() {
        val requestCode = 2020
        val permissions = listOf("one", "two")

        launcher.launchPermissionsRequest(host, permissions, requestCode)

        Truth.assertThat(launcher.requestCodePassed).isEqualTo(requestCode)
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