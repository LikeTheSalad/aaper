package com.likethesalad.android.aaper.strategy

import com.likethesalad.android.aaper.api.data.LaunchMetadata
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.api.launcher.RequestLauncher
import com.likethesalad.android.aaper.api.statusprovider.PermissionStatusProvider
import com.likethesalad.android.aaper.data.RequestCodeLaunchMetadata
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 13/08/20.
 */

@ExtendWith(MockKExtension::class)
class RequestWithCodeMetadataStrategyTest {

    @MockK
    lateinit var host: Any

    private lateinit var requestWithCodeMetadataStrategy: TestRequestWithCodeMetadataStrategy

    @Test
    fun `Get default metadata`() {
        requestWithCodeMetadataStrategy = TestRequestWithCodeMetadataStrategy()

        val result = requestWithCodeMetadataStrategy.getLaunchMetadata(host)

        verifyRequestCodeLaunchMetadata(result!!, 21293)
    }

    @Test
    fun `Get overridden metadata code`() {
        val customCode = 12345
        requestWithCodeMetadataStrategy = TestRequestWithCodeMetadataStrategy(customCode)

        val result = requestWithCodeMetadataStrategy.getLaunchMetadata(host)

        verifyRequestCodeLaunchMetadata(result!!, customCode)
    }

    private fun verifyRequestCodeLaunchMetadata(result: LaunchMetadata, expectedCode: Int) {
        assertThat(result).isInstanceOf(RequestCodeLaunchMetadata::class.java)
        assertThat((result as RequestCodeLaunchMetadata).code).isEqualTo(expectedCode)
    }

    class TestRequestWithCodeMetadataStrategy(val customCode: Int? = null) :
        RequestWithCodeMetadataStrategy<Any>() {

        override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
            throw UnsupportedOperationException()
        }

        override fun getRequestLauncher(host: Any): RequestLauncher<Any> {
            throw UnsupportedOperationException()
        }

        override fun getPermissionStatusProvider(host: Any): PermissionStatusProvider<Any> {
            throw UnsupportedOperationException()
        }

        override fun getRequestCode(): Int {
            return customCode ?: super.getRequestCode()
        }
    }
}
