package com.likethesalad.android.aaper.base.strategies

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.base.LaunchMetadata
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider
import com.likethesalad.android.aaper.api.base.RequestLauncher
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.data.RequestCodeLaunchMetadata
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by César Muñoz on 13/08/20.
 */

@RunWith(MockitoJUnitRunner::class)
class RequestWithCodeMetadataStrategyTest {

    @Mock
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
        Truth.assertThat(result).isInstanceOf(RequestCodeLaunchMetadata::class.java)
        Truth.assertThat((result as RequestCodeLaunchMetadata).code).isEqualTo(expectedCode)
    }

    class TestRequestWithCodeMetadataStrategy(val customCode: Int? = null) :
        RequestWithCodeMetadataStrategy<Any>() {

        override fun onPermissionsRequestResults(host: Any, data: PermissionsResult): Boolean {
            throw UnsupportedOperationException()
        }

        override fun getName(): String {
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