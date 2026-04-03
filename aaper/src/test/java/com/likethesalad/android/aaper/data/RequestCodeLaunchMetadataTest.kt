package com.likethesalad.android.aaper.data

import com.likethesalad.android.aaper.api.data.LaunchMetadata
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by César Muñoz on 13/08/20.
 */

@ExtendWith(MockKExtension::class)
class RequestCodeLaunchMetadataTest {

    private val code = 12345
    lateinit var requestCodeLaunchMetadata: RequestCodeLaunchMetadata

    @BeforeEach
    fun setUp() {
        requestCodeLaunchMetadata = RequestCodeLaunchMetadata(code)
    }

    @Test
    fun `Check if is equal to another metadata with same code`() {
        val other = RequestCodeLaunchMetadata(code)

        assertThat(requestCodeLaunchMetadata.isEqualTo(other)).isTrue()
    }

    @Test
    fun `Check if is equal to another metadata with different code`() {
        val other = RequestCodeLaunchMetadata(123)

        assertThat(requestCodeLaunchMetadata.isEqualTo(other)).isFalse()
    }

    @Test
    fun `Check if is equal to null`() {
        assertThat(requestCodeLaunchMetadata.isEqualTo(null)).isFalse()
    }

    @Test
    fun `Check if is equal to another metadata type`() {
        val metadata = mockk<LaunchMetadata>()

        assertThat(requestCodeLaunchMetadata.isEqualTo(metadata)).isFalse()
    }
}
