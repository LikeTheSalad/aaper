package com.likethesalad.android.aaper.strategies.base

import android.app.Activity
import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.strategies.defaults.launchers.ActivityRequestLauncher
import com.likethesalad.android.aaper.strategies.defaults.statusproviders.ActivityPermissionStatusProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by César Muñoz on 13/08/20.
 */

@RunWith(MockitoJUnitRunner::class)
class ActivityRequestStrategyTest {

    @Mock
    lateinit var host: Activity

    private lateinit var activityRequestStrategy: ActivityRequestStrategy

    @Before
    fun setUp() {
        activityRequestStrategy = object : ActivityRequestStrategy() {
            override fun onPermissionsRequestResults(
                host: Activity,
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
        val result = activityRequestStrategy.getRequestLauncher(host)

        Truth.assertThat(result).isInstanceOf(ActivityRequestLauncher::class.java)
    }

    @Test
    fun `Verify permission status provider type`() {
        val result = activityRequestStrategy.getPermissionStatusProvider(host)

        Truth.assertThat(result).isInstanceOf(ActivityPermissionStatusProvider::class.java)
    }
}