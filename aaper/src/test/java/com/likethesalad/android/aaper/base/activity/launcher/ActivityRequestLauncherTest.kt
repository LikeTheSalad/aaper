package com.likethesalad.android.aaper.base.activity.launcher

import com.likethesalad.android.aaper.testutils.BaseRobolectricTest
import com.likethesalad.android.aaper.testutils.RobolectricActivity
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.Shadows

/**
 * Created by César Muñoz on 13/08/20.
 */

class ActivityRequestLauncherTest : BaseRobolectricTest() {
    private val requestCode = 12345
    private lateinit var activityRequestLauncher: ActivityRequestLauncher

    @Before
    fun setUp() {
        activityRequestLauncher = ActivityRequestLauncher()
    }

    @Test
    fun `Launch permission request from activity`() {
        Robolectric.buildActivity(RobolectricActivity::class.java).use { controller ->
            controller.setup()
            val permissions = listOf("one", "two")

            activityRequestLauncher.launchPermissionsRequest(
                controller.get(),
                permissions,
                requestCode
            )

            val shadowActivity = Shadows.shadowOf(controller.get())

            val lastRequestedPermission = shadowActivity.lastRequestedPermission
            assertThat(lastRequestedPermission.requestedPermissions.asList())
                .containsExactlyElementsOf(permissions)
            assertThat(lastRequestedPermission.requestCode).isEqualTo(requestCode)
        }
    }
}
