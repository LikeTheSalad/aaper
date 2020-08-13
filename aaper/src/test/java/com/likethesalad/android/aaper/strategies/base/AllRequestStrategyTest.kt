package com.likethesalad.android.aaper.strategies.base

import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.strategies.defaults.launchers.ActivityRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.launchers.FragmentRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.ActivityPermissionStatusProvider
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.FragmentPermissionStatusProvider
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.fail
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by César Muñoz on 13/08/20.
 */

@RunWith(MockitoJUnitRunner::class)
class AllRequestStrategyTest {

    @Mock
    lateinit var fragmentHost: Fragment

    @Mock
    lateinit var activityHost: Activity

    private lateinit var allRequestStrategy: AllRequestStrategy

    @Before
    fun setUp() {
        allRequestStrategy = object : AllRequestStrategy() {
            override fun onPermissionsRequestResults(
                host: Any,
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
    fun `Verify request launcher type for fragment host`() {
        val result = allRequestStrategy.getRequestLauncher(fragmentHost)

        Truth.assertThat(result).isInstanceOf(FragmentRequestLauncher::class.java)
    }

    @Test
    fun `Verify permission status provider type for fragment host`() {
        val result = allRequestStrategy.getPermissionStatusProvider(fragmentHost)

        Truth.assertThat(result).isInstanceOf(FragmentPermissionStatusProvider::class.java)
    }

    @Test
    fun `Verify request launcher type for activity host`() {
        val result = allRequestStrategy.getRequestLauncher(activityHost)

        Truth.assertThat(result).isInstanceOf(ActivityRequestLauncher::class.java)
    }

    @Test
    fun `Verify permission status provider type for activity host`() {
        val result = allRequestStrategy.getPermissionStatusProvider(activityHost)

        Truth.assertThat(result).isInstanceOf(ActivityPermissionStatusProvider::class.java)
    }

    @Test
    fun `Verify request launcher exception for unknown host`() {
        try {
            allRequestStrategy.getRequestLauncher(mock())
            fail("Should've gone into the catch block")
        } catch (e: UnsupportedOperationException) {
        }
    }

    @Test
    fun `Verify permission status provider exception for unknown host`() {
        try {
            allRequestStrategy.getPermissionStatusProvider(mock())
            fail("Should've gone into the catch block")
        } catch (e: UnsupportedOperationException) {
        }
    }
}