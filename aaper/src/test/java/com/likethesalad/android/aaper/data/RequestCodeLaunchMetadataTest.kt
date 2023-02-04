package com.likethesalad.android.aaper.data

import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.base.LaunchMetadata
import com.likethesalad.tools.testing.BaseMockable
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 13/08/20.
 */

class RequestCodeLaunchMetadataTest : BaseMockable() {

    private val code = 12345
    lateinit var requestCodeLaunchMetadata: RequestCodeLaunchMetadata

    @Before
    fun setUp() {
        requestCodeLaunchMetadata = RequestCodeLaunchMetadata(code)
    }

    @Test
    fun `Check if is equal to another metadata with same code`() {
        val other = RequestCodeLaunchMetadata(code)

        Truth.assertThat(requestCodeLaunchMetadata.isEqualTo(other)).isTrue()
    }

    @Test
    fun `Check if is equal to another metadata with different code`() {
        val other = RequestCodeLaunchMetadata(123)

        Truth.assertThat(requestCodeLaunchMetadata.isEqualTo(other)).isFalse()
    }

    @Test
    fun `Check if is equal to null`() {
        Truth.assertThat(requestCodeLaunchMetadata.isEqualTo(null)).isFalse()
    }

    @Test
    fun `Check if is equal to another metadata type`() {
        val metadata = mockk<LaunchMetadata>()

        Truth.assertThat(requestCodeLaunchMetadata.isEqualTo(metadata)).isFalse()
    }
}