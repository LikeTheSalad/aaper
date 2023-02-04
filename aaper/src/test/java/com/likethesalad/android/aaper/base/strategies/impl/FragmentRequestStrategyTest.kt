package com.likethesalad.android.aaper.base.strategies.impl

import androidx.fragment.app.Fragment
import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.defaults.launchers.FragmentRequestLauncher
import com.likethesalad.android.aaper.defaults.statusproviders.FragmentPermissionStatusProvider
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 13/08/20.
 */

class FragmentRequestStrategyTest : BaseMockable() {

    @MockK
    lateinit var host: Fragment

    private lateinit var fragmentRequestStrategy: FragmentRequestStrategy

    @Before
    fun setUp() {
        fragmentRequestStrategy = object : FragmentRequestStrategy() {
            override fun onPermissionsRequestResults(
                host: Fragment,
                data: PermissionsResult
            ): Boolean {
                throw UnsupportedOperationException()
            }

            override fun getName(): String {
                throw UnsupportedOperationException()
            }
        }
    }

    @Test
    fun `Verify request launcher type`() {
        val result = fragmentRequestStrategy.getRequestLauncher(host)

        Truth.assertThat(result).isInstanceOf(FragmentRequestLauncher::class.java)
    }

    @Test
    fun `Verify permission status provider type`() {
        val result = fragmentRequestStrategy.getPermissionStatusProvider(host)

        Truth.assertThat(result).isInstanceOf(FragmentPermissionStatusProvider::class.java)
    }
}