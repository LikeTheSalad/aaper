package com.likethesalad.android.aaper.base.fragment.strategy

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.base.fragment.launcher.FragmentRequestLauncher
import com.likethesalad.android.aaper.base.fragment.statusprovider.FragmentPermissionStatusProvider
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
class FragmentRequestStrategyTest {

    @MockK
    lateinit var host: Fragment

    private lateinit var fragmentRequestStrategy: FragmentRequestStrategy

    @BeforeEach
    fun setUp() {
        fragmentRequestStrategy = object : FragmentRequestStrategy() {
            override fun onPermissionsRequestResults(
                host: Fragment,
                data: PermissionsResult
            ): Boolean {
                throw UnsupportedOperationException()
            }
        }
    }

    @Test
    fun `Verify request launcher type`() {
        val result = fragmentRequestStrategy.getRequestLauncher(host)

        assertThat(result).isInstanceOf(FragmentRequestLauncher::class.java)
    }

    @Test
    fun `Verify permission status provider type`() {
        val result = fragmentRequestStrategy.getPermissionStatusProvider(host)

        assertThat(result).isInstanceOf(FragmentPermissionStatusProvider::class.java)
    }
}
