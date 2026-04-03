package com.likethesalad.android.aaper.base.activity.strategy

import android.app.Activity
import com.likethesalad.android.aaper.api.data.PermissionsResult
import com.likethesalad.android.aaper.base.activity.launcher.ActivityRequestLauncher
import com.likethesalad.android.aaper.base.activity.statusprovider.ActivityPermissionStatusProvider
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
class ActivityRequestStrategyTest {

    @MockK
    lateinit var host: Activity

    private lateinit var activityRequestStrategy: ActivityRequestStrategy

    @BeforeEach
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

        assertThat(result).isInstanceOf(ActivityRequestLauncher::class.java)
    }

    @Test
    fun `Verify permission status provider type`() {
        val result = activityRequestStrategy.getPermissionStatusProvider(host)

        assertThat(result).isInstanceOf(ActivityPermissionStatusProvider::class.java)
    }
}
