package com.likethesalad.android.aaper.strategies.base

import androidx.fragment.app.Fragment
import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.strategies.defaults.launchers.FragmentRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.FragmentPermissionStatusProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by César Muñoz on 13/08/20.
 */

@RunWith(MockitoJUnitRunner::class)
class FragmentRequestStrategyTest {

    @Mock
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