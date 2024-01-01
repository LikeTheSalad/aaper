package com.likethesalad.android.aaper.base.strategies.impl

import android.app.Activity
import com.google.common.truth.Truth
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.defaults.launchers.ActivityRequestLauncher
import com.likethesalad.android.aaper.defaults.statusproviders.ActivityPermissionStatusProvider
import com.likethesalad.android.aaper.strategy.impl.ActivityRequestStrategy
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

/**
 * Created by César Muñoz on 13/08/20.
 */

class ActivityRequestStrategyTest : BaseMockable() {

    @MockK
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